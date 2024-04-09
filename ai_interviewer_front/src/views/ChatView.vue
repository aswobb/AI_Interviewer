<template>
  <div id="app">
    <audio ref="audioPlayer"></audio>
    <el-row class="title">
      SNSソフト・AI面接官
      <el-dropdown class="title-menu">
        <span class="el-dropdown-link"> ☰ </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="showPersonalInfo">個人情報</el-dropdown-item>
          <el-dropdown-item @click.native="logout">ログアウト</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-row>
    <el-row class="content">
      <el-col class="bot-message" 
      :xl="getBreakpointConfig('xl',false)"
      :lg="getBreakpointConfig('lg',false)" 
      :md="getBreakpointConfig('md',false)" 
      :sm="getBreakpointConfig('sm',false)" 
      :xs="getBreakpointConfig('xs',false)" >
        <v-card-text>
          こんにちは、AI面接官です。<br>
          あなたの面接を担当させていただきます。よろしくお願いいたします。
        </v-card-text> 
      </el-col>

      <el-col :span="24" class="bot-message"
      :xl="getBreakpointConfig('xl',false)"
      :lg="getBreakpointConfig('lg',false)" 
      :md="getBreakpointConfig('md',false)" 
      :sm="getBreakpointConfig('sm',false)" 
      :xs="getBreakpointConfig('xs',false)" >
        <v-card-text>
          ではまずは、面接で重視したいポイントを教えていただけますか？<br>
          <label v-for="(item, index) in checkBoxes" :key="index">
            <input type="checkbox" :id="index" v-model="item.checked" @change="check" :disabled="isCheckboxDisabled">
            {{ item.point }}<br>
          </label>
          <label key="checkAll">
            <input type="checkbox" id="checkAll" v-model="checkboxAll" @change="checkAll" :disabled="isCheckboxDisabled">
            全て<br>
          </label>
          
          <br>チェックボックスで選択したら送信ボタンを押してください。
        </v-card-text>
      </el-col>
      
      
      <el-col
        v-for="(message, index) in renderMessages"
        :key="index"
        :xl="getBreakpointConfig('xl',message.isUser)"
        :lg="getBreakpointConfig('lg',message.isUser)" 
        :md="getBreakpointConfig('md',message.isUser)" 
        :sm="getBreakpointConfig('sm',message.isUser)" 
        :xs="getBreakpointConfig('xs',message.isUser)" 
        :class="{
          'user-message': message.isUser,
          'bot-message': !message.isUser,
        }"
      >
        <v-card-text v-html="parseHTML(message.text)"> </v-card-text>
      </el-col>
    </el-row>
    <el-row class="footer footer-row">
      <el-col :span="16">
        <el-input
          type="textarea"
          v-model="userMessage"
          @keydown.enter.prevent="onEnterPress"
          @compositionstart="handleCompositionStart"
          @compositionend="handleCompositionEnd"
          :placeholder="getPlaceholderText"
          class="custom-input-style"
          :disabled="isInputDisabled"
        ></el-input>
      </el-col>
      <el-col :span="2">
        <el-button
          :class="[
            userMessage.trim() ? 'custom-purple-button' : 'light-purple-button',
          ]"
          @click="sendMessage"
          :disabled="isButtonDisabled || isLoading"
        >
          <template v-if="isLoading">
            <!-- 这里可以使用Element UI的Loading组件或自定义等待图标 -->
            <i class="el-icon-loading"></i>
          </template>
          <template v-else> 送信 </template>
        </el-button>
        <!-- 音声入力試験用-->
        <q-btn @click="toggleSpeechRecognition"  :label="speechRecognitionActive ? '音声入力停止' : '音声入力開始'" color="primary" :style="{ width: '130px', height: '45px'}"class="btn-spacing"/>
        <!-- 音声入力試験用-->
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      userMessage: "",
      renderMessages: [], // 画面に表示させるメッセージを格納
      messages: [], // chatAPIに投げるメッセージを格納
      isInputNotEmpty: false,
      showPrompt: false,
      isInputDisabled: false,
      isButtonDisabled: false,
      isLoading: false,
      composing: false, // 跟踪输入法状态
      audioURL: '', // 存储音频的URL
      speechRecognitionActive: false, // 记录语音输入是否激活
      recognition: null ,// 存储语音识别对象
      checkboxAll: false,       // 「全て」のチェックボックスがチェックされているかどうか
      checkBoxes: [             // チェックボックス制御用の変数、この変数に格納されているオブジェクトを追加すればチェックボックス増やせるはずです。
        {checked: false, point: "技術スキル"},
        {checked: false, point: "コミュニケーション能力"},
        {checked: false, point: "SE経験"},
        // {checked: false, point: "追加したい重視ポイント"},
        ],
      
      responsiveDesignSettings: { // 画面の大きさに応じてメッセージボックスの大きさ・位置を変える
        botMessage: {
          xl: {offset: 4, span:7},
          lg: {offset: 3, span:8},
          md: {offset: 3, span:10},
          sm: {offset: 3, span:12},
          xs: {offset: 2, span:17},
        },
        userMessage: {
          xl: {offset: 13, span:7},
          lg: {offset: 13, span:8},
          md: {offset: 11, span:10},
          sm: {offset: 9, span:12},
          xs: {offset: 4, span:18},
        }
      },

      isCheckboxDisabled: false,    // チェックボックスの入力制御
      isRequestingCSVData: false,   // CSVデータ要求中かどうか
      tmp: 0, //デバッグ用
    };
  },
  computed: {
    getPlaceholderText(){
      if (this.isCheckboxDisabled){
        // チェックボックスが入力できないとき
        if (this.isInputDisabled && this.isRequestingCSVData){
          // Inputが無効になっている かつ CSVを生成中の時はCSVデータ生成中ですの文字列を出力
          return "CSVデータ生成中です。";
        } else if (this.isInputDisabled) {
          // Inputが無効になっている場合は、入力が無効になっている旨を出力
          return "入力が無効になっています";
        } else {
          // Inputが有効になっている場合は、メッセージを入力の文字列を出力
          return "メッセージを入力";
        }
      } else {
        // チェックボックスが入力できる状態であればチェックボックスで入力させる
        return "チェックボックスで選択してください"
      }
    }
  },
  methods: {
    onEnterPress() {
      if (!this.composing) {
        this.sendMessage();
      }
    },
    handleCompositionStart() {
      this.composing = true; // 输入法开始输入
    },
    handleCompositionEnd(event) {
      this.composing = false; // 输入法结束输入
    },
    checkForNewLine(event) {
      if (event.shiftKey && event.key === "Enter") {
        return;
      }
      if (event.key === "Enter" && !event.shiftKey) {
        if (!this.composing && this.isComposingEnded) {
          event.preventDefault(); // 阻止回车键的默认行为
          this.sendMessage();
          this.isComposingEnded = false; // 重置标志
        }
      }
    },
    inputToUserMessageFromCheckbox(){
      let msg = "";
      // チェックされている項目を,区切りで結合(String)
      if (this.checkBoxes.filter((item) => item.checked).length == 0) {
        // 何もチェックされているものがなければ選択してもらうように促す -> pleaseholderで出力しているのでuserMessageには格納しない。
        msg = "";
      } else {
        let checkedPoints = this.checkBoxes.filter((item) => item.checked).map((item)=>item.point).join(",");
        msg = "面接で重視するポイントは" + checkedPoints + "です。";
      }
      this.userMessage = msg;
    },
    check(){
      // 「全て」のチェックボックスは
      // 全部チェックされている時にしかチェックできないようにする
      // this.checkBoxes.map((item)=>console.log("checked: " + item.checked + ", point: " + item.point));
      this.checkboxAll = this.checkBoxes.filter((item)=>!item.checked).length == 0 ? true : false;
      this.inputToUserMessageFromCheckbox(); // チェックした内容をテキストボックスに転記
    },
    checkAll(){
      // 「全て」のチェックボックスが入力された時の、そのほかのチェックボックスの値切り替え処理
      if (this.checkboxAll){
        this.checkBoxes = this.checkBoxes.map((item) => {
          item.checked = true;
          return item;
        });
      } else {
        this.checkBoxes = this.checkBoxes.map((item) => {
          item.checked = false;
          return item;
        });
      }
      // this.checkBoxes.map((item)=>console.log("checked: " + item.checked + ", point: " + item.point));
      this.inputToUserMessageFromCheckbox(); // チェックしている内容をテキストボックスに転記
    },
    async sendMessage() {
      if (this.userMessage.trim() !== "") {
        if (!this.isCheckboxDisabled){
          this.isCheckboxDisabled = true;
        }
        // 开始加载时设置为true
        this.isLoading = true;
        // 在发送请求前禁用输入框
        this.isInputDisabled = true;
        this.isButtonDisabled = true;
        this.renderMessages.push({
          text: this.userMessage,
          isUser: true,
        });
        this.messages.push({
          text: this.userMessage,
          isUser: true,
        })
        // 使用 $nextTick 确保 DOM 更新后再滚动
        this.$nextTick(() => {
          this.scrollToBottom();
        });
        try {
          var localPath = this.GLOBAL.localSrc;
          const token = localStorage.getItem("token");
          const chatBody = this.messages.map(rowData => { // {text: String, isUser: Boolean}の形
            const role = rowData.isUser ? "user" : "assistant";
            return JSON.stringify({role: role, content: rowData.text});
          }).join(',');
          // 发送 API 请求
          const response = await axios.post(
            "/api/chat/sendMessageByGoogleCloud",
            {
              message: chatBody,
            },
            {
              headers: {
                "Content-Type": "application/json",
                token: token,
              },
            }
          );
          // デバッグ用
          // let response; 
          // if (this.tmp == 0){
          //   response = await this.sleep(500).then( () => {return {data: {state: 20000, data: "質問1:\n朝ごはんは何ですか"}}}); // 0.5秒後にテストデータを返す.
          // } else {
          //   response = await this.sleep(500).then( () => {return {data: {state: 20000, data: "以上で面接を終了します。"}}}); // 0.5秒後にテストデータを返す.
          // }
          // this.tmp += 1;
          // デバッグ用ここまで
          this.$gtm.sendCustomEvent("send_message");
          // 处理 API 响应
          if (response.data && response.data.state === 20000) {
            const botResponse = response.data.data;
            const content = botResponse.content;
            const formattedContent = content.replace(/\\n/g, "\n");
            const audioContent = botResponse.audioContent;

            // 创建一个 Blob 对象
            const blob = new Blob([this.base64ToArrayBuffer(audioContent)], { type: 'audio/wav' });
            
            // 使用 URL.createObjectURL() 创建音频的 URL
            this.audioURL = URL.createObjectURL(blob);

            // 设置音频的源
            this.$refs.audioPlayer.src = this.audioURL;

            // 播放音频
            this.$refs.audioPlayer.play();

            // 将机器人的响应添加到 messages 数组中
            this.renderMessages.push({
              text: formattedContent,
              isUser: false,
            });
            this.messages.push({
              text: formattedContent,
              isUser: false,
            })
            // 确保在DOM更新后执行滚动操作
            this.$nextTick(() => {
              this.scrollToBottom();
            });
          } else {
            console.error("API response error:", response.data);
            this.renderMessages.push({
              text: response.data.message,
              isUser: false,
            });
          }
        } catch (error) {
          console.error("API request error:", error);
          this.$notify.error({
            title: "失敗しました.",
            message: error,
          });
        }
        // 加载完成后设置为false
        this.isLoading = false;
        this.isInputDisabled = false;
        this.isButtonDisabled = false;
        this.userMessage = "";
      }
    },
    // 将 Base64 编码的字符串转换为 ArrayBuffer
    base64ToArrayBuffer(base64) {
      const binaryString = window.atob(base64);
      const bytes = new Uint8Array(binaryString.length);
      for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i);
      }
      return bytes.buffer;
    },
    scrollToBottom() {
      const content = this.$el.querySelector(".content");
      if (content) {
        content.scrollTop = content.scrollHeight;
      }
    },
    showPersonalInfo() {
      // 处理显示个人信息的逻辑
      console.log("132");
      this.$gtm.sendCustomEvent("show_personal_info");
    },
    logout() {
      console.log("132");
      // 清除本地存储中的 Token
      localStorage.removeItem("token"); // 或者使用 sessionStorage
      this.$gtm.sendCustomEvent("log_out");
      sessionStorage.removeItem("username");
      this.$router.push("/");
    },
    parseHTML: function (message) {
      return message.replace(/\n/g, "<br>"); // 将换行符替换为HTML的<br>标签
    },
    async sleep(ms){
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    padZero(num) {
      return num < 10 ? '0' + num : num;
    },
    async generateDownloadLink(){
      this.isRequestingCSVData = true;
      // 开始加载时设置为true
      this.isLoading = true;
      // 在发送请求前禁用输入框
      this.isInputDisabled = true;
      this.isButtonDisabled = true;
      console.log("csv要求");
      try {
        const currentDate = new Date();
        const formattedDate = currentDate.getFullYear().toString() + 
                              this.padZero(currentDate.getMonth() + 1) + 
                              this.padZero(currentDate.getDate()) +
                              this.padZero(currentDate.getHours()) +
                              this.padZero(currentDate.getMinutes()) +
                              this.padZero(currentDate.getSeconds());                                
        const csvFileName = "QAData_" + formattedDate + ".csv"; // CSVファイル名
        const token = localStorage.getItem("token");
        const chatBody = this.messages.map(rowData => { // rowData={text: String, isUser: Boolean}の形
            const role = rowData.isUser ? "user" : "assistant";
            return JSON.stringify({role: role, content: rowData.text});
          }).join(',');
        const response = await axios.post(
        "/api/chat/sendMessage",
        {
          message: chatBody + ",{\"role\":\"user\", \"content\":\"CSVインポートしたいので面接での質問と回答を出力してください。必ず「質問」と「回答」の2列にしてください。CSV以外の出力はしないでください。CSVデータとそうではないところがわかるように、CSVデータは```で囲ってください。\" }"
        },
        {
          headers: {
            "Content-Type": "application/json",
            token: token,
          },
        });
        // デバッグ用
        // let response; 
        // response = await this.sleep(500).then( () => {return {data: {state: 20000, data: "以下がCSVファイルです。\n```\n質問,回答\nほげ1,ふが1\n今日の朝ごはん,卵焼き\n```\n"}}}); // 0.5秒後にテストデータを返す.
        // デバッグ用ここまで
        if (response.data && response.data.state === 20000) {
          // console.log("response : " + response.data.data);
          const csvData = response.data.data.match(/```([\s\S]*?)```/g).slice(-1)[0].replace(/(```.*\n*|^\n$|^[^,]+$)/g,""); // ```もしくは、空行もしくは、，が含まれない行は空文字にリプレイス
          // console.log("csvData : " + csvData);
          const encodeData = new TextEncoder('utf-8').encode('\ufeff' + csvData); 
          const blob = new Blob([encodeData], {type: "text/csv;charset=utf-8"}); // CSVデータ作成
          const blobUrl = URL.createObjectURL(blob);  // ダウンロードリンク作成
          // console.log(blobUrl);
          this.renderMessages.push({
            text: "質問と回答をCSVにまとめました。\nダウンロードリンクは<a href=" + blobUrl + " download=\"" + csvFileName + "\">こちら</a>",
            isUser: false
          })
          this.$nextTick(() => {
              this.scrollToBottom();
            });
          this.isButtonDisabled = true; // 送信ボタンOFF
          this.isInputDisabled = true; // 入力をさせない
          this.isLoading = false;
          this.isRequestingCSVData = false;
        } else {
          console.error("API response error:", response.data);
          this.renderMessages.push({
            text: response.data.message,
            isUser: false,
          });
        }
      } catch (error) {
        console.error("API request error:", error);
        this.$notify.error({
          title: "失敗しました.",
          message: error,
        });
      }
    },
    getBreakpointConfig(breakpoint, isUserMessage){
      // isUserMessageに応じて動的にspanとoffsetを変更するロジックを追加
      if (isUserMessage){
        // ユーザーメッセージの場合
        return this.responsiveDesignSettings.userMessage[breakpoint];
      } else {
        // ボットメッセージの場合
        return this.responsiveDesignSettings.botMessage[breakpoint];
      }
    },
    // 音声入力試験用
    async startSpeechRecognition() {
    try{  
      this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const recognition = new webkitSpeechRecognition(); // 创建语音识别对象
      recognition.lang = 'ja-JP'; // 日本語に設定
      recognition.interimResults = false;
      recognition.start(); // 开始语音识别

      recognition.onresult = (event) => { // 当识别完成时
        this.userMessage = event.results[0][0].transcript; // 将识别结果赋值给transcript变量
      }

      this.recognition.onend = () => {
        this.stopSpeechRecognition();
      }
    }catch (error) {
        console.error('権限取得失敗しました：', error);
        // 处理获取媒体权限失败的情况
      }
    },

    stopSpeechRecognition() {
      if (this.recognition !== null) {
        this.recognition.stop(); // 停止语音识别
        this.recognition = null; // 释放语音识别对
      }
      if (this.stream !== null) {
        this.stream.getTracks().forEach(track => track.stop());
        this.stream = null; // 将stream置为null，释放资源
      }
    },
    toggleSpeechRecognition() {
      if (!this.speechRecognitionActive) {
        // 开始语音识别
        this.speechRecognitionActive = !this.speechRecognitionActive;
        this.startSpeechRecognition();
      } else {
        // 停止语音识别
        this.speechRecognitionActive = !this.speechRecognitionActive;
        this.stopSpeechRecognition();
      }
    
    },

  },


  watch: {
    userMessage(newVal) {
      this.isInputNotEmpty = newVal.trim() !== "";
    },
    messages(){
      let addedMessage = this.messages.slice(-1)[0]; // 一番直近に追加されたメッセージ
      if (addedMessage.text.match(/以上で.*面接[はを]終了します/g)){
        // 面接終了の処理記載
        // csvデータ作成 → ダウンロードリンク作成 → これ以上の質問を受け付けないようにする
        // CSVデータもChatGPTに作成してもらう。
        this.generateDownloadLink();
      }
    },
  },
  mounted: function() {
    // 読み込まれたらdatalayerにloginid書き出し
    dataLayer = [{
      login_id: sessionStorage.getItem('username') || ''
    }]
    // 最初のメッセージはAI側から
    let firstMessage = "こんにちは、AI面接官です。\nあなたの面接を担当させていただきます。本日はよろしくお願いいたします。"
    // 最初のメッセージはrenderMessagesには格納せずに<template>の中にべた書き
    // chatAPIにはこのメッセージも含めて投げたいのでmessagesには格納しておく
    this.messages.push({
      text: firstMessage,
      isUser: false,
    })

    // 面接のポイント選択
    let secondMessage = "ではまずは、面接で重視したいポイントを教えていただけますか？\n" + 
                        "例えば、技術スキル、コミュニケーション能力、SE経験 などです。";
    
    // 2番目のメッセージもrenderMessagesには格納せずに<template>の中にべた書き
    // chatAPIにはこのメッセージも含めて投げたいのでmessagesには格納しておく
    this.messages.push({
      text: secondMessage,
      isUser: false,
    });
    this.isInputDisabled = true;

  }
};
</script>
<style >
body,
html {
  background-color: #fae6f9 !important;
  font-family: "Arial", sans-serif;
  height: 100%;
  margin:0;
  padding: 0;
  box-sizing: border-box;
  overflow: hidden;
}
</style>
<style scoped>
#app {
  background-size: cover;
  max-width: 100%;
  min-width: 0;
  height: auto;
  margin: 0 auto;
}

.title {
  max-width: 90%;
  height: 3rem;
  line-height: 3rem;
  text-align: center;
  background-color: #138f6a;
  color: #ffffff;
  font-size: 1.5rem;
  border-radius: 50px;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1;
  position: relative;
}

.title-menu {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  font-size: 1.5rem;
  cursor: pointer;
  color: white;
}

#app .el-dropdown .el-dropdown-menu .el-dropdown-item {
  font-weight: bold;
}

.content {
  display: flex;
  flex-direction: column;
  margin-top: 3rem;
  padding-bottom: 5rem;
  overflow-y: auto;
  max-height: calc(100vh - 7rem);
}

.user-message,
.bot-message {
  word-wrap: break-word;
  border-radius: 18px;
  padding: 10px 20px;
  box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.15);
  margin-bottom: 1rem;
  background-color: white;
  border: 1px solid #eaeaea;
} 

.user-message {
  background-color: #dddddd;
  color: #000000;
}

.bot-message {
  background-color: #a6e1fa;
  color: #000000;
}


.footer {
  width: 100%;
  height: 4rem;
  font-size: 2rem;
  border-radius: 50px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  align-items: center;
  padding: 0;
}

.footer-row {
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.footer > .el-col {
  justify-content: center;
  align-items: center;
  margin: 0;
  padding: 0;
}

.footer > .el-col:nth-child(1) {
  margin-right: 10px;
}

.footer > .el-col:nth-child(2) {
  margin-right: 5px;
}

.footer > .el-col:nth-child(3) {
  margin-left: 5px;
}

.footer > .el-col:nth-child(1),
.footer > .el-col:nth-child(3) {
  margin: 0 5px;
}

.custom-input-style .el-input__inner {
  width: 100%;
  border-radius: 30px;
  border-color: #d3bce2;
  color: #000000;
  min-height: 36px;
  height: 36px;
  margin: 0;
  padding: 4px 10px; /* 调整内部填充以垂直居中文本 */
  overflow-y: hidden; /* 隐藏溢出的文本 */
  font-size: 16px; /* 根据需要调整字体大小 */
  line-height: 1.5; /* 根据需要调整行高 */
}

.custom-input-style .el-input__inner::placeholder {
  color: #808080;
}

.custom-input-style .el-input__inner:-ms-input-placeholder {
  color: #808080;
}

.custom-input-style .el-input__inner::-moz-placeholder {
  color: #808080;
  opacity: 1;
}

.custom-purple-button {
  background-color: #8f138f;
  color: white;
}

.light-purple-button {
  background-color: #d3bce2;
  color: purple;
}

.custom-purple-button,
.light-purple-button {
  border-radius: 25px;
  padding: 10px 15px;
  font-size: 1.2rem;
}

/* 添加等待图标的样式 */
 /* .el-icon-loading { */
  /* 这里可以自定义样式，例如旋转动画等 */
/* } */ 

input[type="file"] {
  display: none;
}

@media (max-width: 767px) {
  body,
  html {
    font-size: 13px;
  }

  .content {
    padding-top: 3.5rem;
    padding-bottom: 5rem;
  }

  .title,
  .footer {
    width: 100%;
    position: relative;
  }

  /* .user-message,
  .bot-message {
    max-width: 90%;
  } */

  .footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 50px;
    padding: 5px;
    display: flex;
    justify-content: space-around;
    align-items: center;
    box-sizing: border-box;
  }

  .footer > .el-col {
    padding: 0 5px;
  }

  .file-upload-button {
    flex: 0 0 36px;
    padding: 0;
  }

  .custom-input-style .el-input__inner {
    flex-grow: 1;
    flex-shrink: 1;
    flex-basis: 60%;
    min-width: 150px;
    margin: 0 5px;
  }

  .custom-purple-button,
  .light-purple-button {
    flex: 0 0 72px;
    padding: 0 20px;
  }
  /* 调整输入框和按钮的大小以适应小屏幕 */
  .custom-input-style .el-input__inner,
  .custom-purple-button,
  .light-purple-button {
    min-height: 3rem; /* 增加高度以便于触摸 */
  }
}

/* 以下テストコード */
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}

.btn-spacing {
  margin-bottom: 50px;
  margin-top:10px;
  white-space: nowrap;
  margin-right: 30px;
  padding-left: 10px;
}
</style>
