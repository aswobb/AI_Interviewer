<template>
  <div id="app">
    <audio ref="audioPlayer"></audio>
    <!-- <el-row class="title">
      {{ pageTitle + '・AI面接官' }}
      <el-dropdown class="title-menu">
      </el-dropdown>
    </el-row> -->
    <el-row class="content">
      <el-col class="bot-message" :xl="getBreakpointConfig('xl', false)" :lg="getBreakpointConfig('lg', false)"
        :md="getBreakpointConfig('md', false)" :sm="getBreakpointConfig('sm', false)"
        :xs="getBreakpointConfig('xs', false)">

        <v-card-text>
          こんにちは、AI面接官です。<br>
          あなたの面接を担当させていただきます。よろしくお願いいたします。
        </v-card-text>

      </el-col>



      <el-col :span="24" class="bot-message" :xl="getBreakpointConfig('xl', false)"
        :lg="getBreakpointConfig('lg', false)" :md="getBreakpointConfig('md', false)"
        :sm="getBreakpointConfig('sm', false)" :xs="getBreakpointConfig('xs', false)">
        <v-card-text>
          ではまずは、面接で重視したいポイントを教えていただけますか？<br>
          <label v-for="(item, index) in checkBoxes" :key="index">
            <input type="checkbox" :id="index" v-model="item.checked" @change="check" :disabled="isCheckboxDisabled">
            {{ item.point }}<br>
          </label>
          <label key="checkAll">
            <input type="checkbox" id="checkAll" v-model="checkboxAll" @change="checkAll"
              :disabled="isCheckboxDisabled">
            全て<br>
          </label>

          <br>チェックボックスで選択したら送信ボタンを押してください。
        </v-card-text>
      </el-col>


      <el-col v-for="(message, index) in renderMessages" :key="index" :xl="getBreakpointConfig('xl', message.isUser)"
        :lg="getBreakpointConfig('lg', message.isUser)" :md="getBreakpointConfig('md', message.isUser)"
        :sm="getBreakpointConfig('sm', message.isUser)" :xs="getBreakpointConfig('xs', message.isUser)" :class="{
          'user-message': message.isUser,
          'bot-message': !message.isUser,
        }">
        <v-card-text v-html="parseHTML(message.text)"> </v-card-text>
      </el-col>

    </el-row>
    <el-row class="footer footer-row">
      <el-col>
        <p style="font-size: 13px ;color: #ff0000;margin-left: 180px;" v-if="!isCheckboxDisabled"
          v-show="messages.length == 2">
          *まずはチェックボックスで選択してください</p>
        <div class="container">
          <el-input @keydown.enter.prevent="onEnterPress" :disabled="isInputDisabled || disabledByConfim"
            :placeholder="getPlaceholderText" @compositionstart="handleCompositionStart"
            @compositionend="handleCompositionEnd" v-model="userMessage" type="textarea" class="long-input" />
          <div class="button-group">
            <el-button type="green" @click="sendMessage" :disabled="isButtonDisabled || isLoading || disabledByConfim"
              :icon="isLoading ? '' : 'el-icon-s-promotion'
                " class="action-button" round>
              <template v-if="isLoading">
                <!-- 这里可以使用Element UI的Loading组件或自定义等待图标 -->
                <i class="el-icon-loading"></i>
              </template>
              送信</el-button>
            <el-button :type="!speechRecognitionActive ? 'green' : 'danger'" class="action-button" round
              :disabled="isInputDisabled || disabledByConfim" @click="toggleSpeechRecognition" color="purple-7">
              {{ speechRecognitionActive ? '音声停止' : '音声入力' }}</el-button>
          </div>
          <el-dialog :show-close="false" :close-on-click-modal="false" append-to-body title="履歴書アップロードしてください"
            :visible.sync="dialogVisible" width="30%">
            <span>
              <input type="file" @change="handleFileUpload" />

            </span>
            <span slot="footer" class="dialog-footer">
              <el-button type="primary" @click="submitFile">アップロード</el-button>
              <el-button @click="cancleUpload">キャンセル</el-button>
            </span>
          </el-dialog>

          <el-dialog append-to-body title="選択してください" :visible.sync="flag" width="30%" :show-close="false"
            :close-on-click-modal="false">
            <p>{{ memberVo.uploadStatus == 1 ?
              "システムはご履歴書を確認いたしましたが、新しい履歴書をアップロードしますか？それとも既存の履歴書で面接を続けますか？" :
              "システムはあなたの履歴書を検出できませんでした。履歴書をアップロードしますか、テキスト形式の履歴書をアップロードしますか？" }}</p>
            <span slot="footer" class="dialog-footer">
              <el-button type="primary" @click="reUpload">アップロード</el-button>
              <el-button v-show="memberVo.uploadStatus == 1" @click="original">既存の使う</el-button>
              <el-button @click="goOn">テキスト入力</el-button>
            </span>
          </el-dialog>
        </div>
      </el-col>
    </el-row>
    <div>


    </div>
  </div>
</template>

<script>
import axios from "axios";
import { Message } from "element-ui";
import { chatMessageSend1, fileSend, MessageSend2, MessageSend3 } from '@/api'
export default {
  data() {
    return {
      voices: [],
      selectedVoice: null,
      disabledByConfim: false,
      flag: false,//面接者に新しい履歴書をアップロードするかどうか選んでもらう
      memberVo: {},
      dialogVisible: false,
      uploadFlag: false,
      selectedFile: null,
      pageTitle: '',
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
      recognition: null,// 存储语音识别对象
      checkboxAll: false,       // 「全て」のチェックボックスがチェックされているかどうか
      checkBoxes: [             // チェックボックス制御用の変数、この変数に格納されているオブジェクトを追加すればチェックボックス増やせるはずです。
        { checked: false, point: "技術スキル" },
        { checked: false, point: "コミュニケーション能力" },
        { checked: false, point: "プロジェクト管理能力" },
        { checked: false, point: "問題解決能力" },
        { checked: false, point: "チームワーク" },
        { checked: false, point: "セキュリティ意識と対策" },
        { checked: false, point: "リーダーシップ" },
        { checked: false, point: "継続的な学習と技術的成長への意欲" }
        // {checked: false, point: "追加したい重視ポイント"},
      ],

      responsiveDesignSettings: { // 画面の大きさに応じてメッセージボックスの大きさ・位置を変える
        botMessage: {
          xl: { offset: 4, span: 7 },
          lg: { offset: 3, span: 8 },
          md: { offset: 3, span: 10 },
          sm: { offset: 3, span: 12 },
          xs: { offset: 2, span: 17 },
        },
        userMessage: {
          xl: { offset: 13, span: 7 },
          lg: { offset: 13, span: 8 },
          md: { offset: 11, span: 10 },
          sm: { offset: 9, span: 12 },
          xs: { offset: 4, span: 18 },
        }
      },

      isCheckboxDisabled: false,    // チェックボックスの入力制御
      isRequestingCSVData: false,   // CSVデータ要求中かどうか
      tmp: 0, //デバッグ用
    };
  },
  created() {
    this.memberVo = this.$store.state.companyMemberInfo
    this.getVoice()
  },
  computed: {
    getPlaceholderText() {
      if (this.isCheckboxDisabled) {
        // チェックボックスが入力できないとき
        if (this.isInputDisabled && this.isRequestingCSVData) {
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
    getVoice() {
      // 获取所有语音对象
      this.voices = window.speechSynthesis.getVoices();

      // 尝试自动选择 Microsoft Ichiro - Japanese (Japan)
      this.selectedVoice = this.voices.find(voice => voice.name === 'Microsoft Ichiro - Japanese (Japan)');

      // 如果没找到，再重新获取语音对象
      if (!this.selectedVoice) {
        window.speechSynthesis.onvoiceschanged = () => {
          this.voices = window.speechSynthesis.getVoices();
          this.selectedVoice = this.voices.find(voice => voice.name === 'Microsoft Ichiro - Japanese (Japan)');
        };
      }
    },
    cancleUpload() {
      this.dialogVisible = false
      this.flag = true
    },
    async original() {
      this.flag = false
      this.renderMessages.push({
        text: "既存の履歴書を使いたい",
        isUser: true,
      });
      const userMessage = "下記は私の履歴書です" + this.memberVo.resume
      this.messages.push({
        text: userMessage,
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
          return { role: role, content: rowData.text };
        });
        // 发送 API 请求
        //
        //
        const response = await chatMessageSend1(chatBody);
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
        }
        // else if (response.data.state == 40400) {
        //   this.$router.push("/interview/user/login")
        //   this.$notify.warning({
        //     message: 'ログインが期限切れです,再度ログインしてください',
        //     type: 'warn'
        //   });
        // } 
        else {
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
      this.disabledByConfim = false
    },
    goOn() {
      this.flag = false
      this.renderMessages.push({
        text: 'ご承知いたしました、ごスキルシートを入力しください',
        isUser: false,
      });
      this.$nextTick(() => {
        this.scrollToBottom();
      });
      this.disabledByConfim = false
      // 创建一个新的 SpeechSynthesisUtterance 实例
      const utterance = new SpeechSynthesisUtterance("ご承知いたしました、ごスキルシートを入力しください");
      utterance.voice = this.selectedVoice;
      utterance.lang = 'ja-JP'; // 设置语言为日语
      window.speechSynthesis.speak(utterance);
    },
    reUpload() {
      this.flag = false
      this.dialogVisible = true
    },
    handleFileUpload(event) {
      this.selectedFile = event.target.files[0];
    },
    async submitFile() {

      if (!this.selectedFile) {
        Message.warning("先に、ご履歴書選択してください！")
        return
      }
      this.isInputDisabled = !this.isInputDisabled
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      this.determineFileType(this.selectedFile)
      try {
        const response = await fileSend(formData)
        this.messages.push({
          text: response.data.data,
          isUser: true,
        })
        Message.success("履歴書を解析しました！")
        this.dialogVisible = false
        this.renderMessages.push({
          text: '履歴書アップロードが完了しました',
          isUser: true,
        });
        this.$nextTick(() => {
          this.scrollToBottom();
        });
        console.log(210, this.messages);
        const chatBody = this.messages.map(rowData => { // {text: String, isUser: Boolean}の形
          const role = rowData.isUser ? "user" : "assistant";
          return { role: role, content: rowData.text };
        });
        // 发送 API 请求
        const response1 = await chatMessageSend1(chatBody)
        if (response1.data && response1.data.state === 20000) {
          const botResponse = response1.data.data;
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
          this.isInputDisabled = !this.isInputDisabled
          this.disabledByConfim = false
          // 确保在DOM更新后执行滚动操作
          this.$nextTick(() => {
            this.scrollToBottom();
          });
        }
        //  if (response.data.state == 40400) {
        //   this.$router.push("/interview/user/login")
        //   this.$notify.warning({
        //     message: 'ログインが期限切れです,再度ログインしてください',
        //     type: 'warn'
        //   });
        // } 
        else {
          console.error("API response error:", response.data);
          this.renderMessages.push({
            text: response.data.message,
            isUser: false,
          });
        }
      } catch (error) {
        Message.error('履歴書アップロード失敗しました')
      }

    },

    determineFileType(file) {
      const fileName = file.name.toLowerCase();
      const mimeType = file.type;

      const allowedTypes = [
        'application/pdf',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/msword'
      ];

      if (!allowedTypes.includes(mimeType)) {
        Message.warning("WORD、EXCELまたはPDF形式の履歴書をアップロードしてください！")
        throw new Error('条件不满足，请求终止');
      }
      // if (!(mimeType === 'application/pdf' || fileName.endsWith('.pdf')) && !(
      //   mimeType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
      //   mimeType === 'application/vnd.ms-excel' ||
      //   fileName.endsWith('.xls') ||
      //   fileName.endsWith('.xlsx'))) {
      //   Message.warning("EXCELまたはPDF形式の履歴書をアップロードしてください！")
      //   throw new Error('条件不满足，请求终止');
      // }
    },
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
    inputToUserMessageFromCheckbox() {
      let msg = "";
      // チェックされている項目を,区切りで結合(String)
      if (this.checkBoxes.filter((item) => item.checked).length == 0) {
        // 何もチェックされているものがなければ選択してもらうように促す -> pleaseholderで出力しているのでuserMessageには格納しない。
        msg = "";
      } else {
        let checkedPoints = this.checkBoxes.filter((item) => item.checked).map((item) => item.point).join(",");
        msg = "面接で重視するポイントは" + checkedPoints + "です。";
      }
      this.userMessage = msg;
    },
    check() {
      // 「全て」のチェックボックスは
      // 全部チェックされている時にしかチェックできないようにする
      // this.checkBoxes.map((item)=>console.log("checked: " + item.checked + ", point: " + item.point));
      this.checkboxAll = this.checkBoxes.filter((item) => !item.checked).length == 0 ? true : false;
      this.inputToUserMessageFromCheckbox(); // チェックした内容をテキストボックスに転記
    },
    checkAll() {
      // 「全て」のチェックボックスが入力された時の、そのほかのチェックボックスの値切り替え処理
      if (this.checkboxAll) {
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
        if (!this.isCheckboxDisabled) {
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
            return { role: role, content: rowData.text };
          });
          // 发送 API 请求
          const response = await chatMessageSend1(chatBody);
          // デバッグ用
          // let response; 
          // if (this.tmp == 0){
          //   response = await this.sleep(500).then( () => {return {data: {state: 20000, data: "質問1:\n朝ごはんは何ですか"}}}); // 0.5秒後にテストデータを返す.
          // } else {
          //   response = await this.sleep(500).then( () => {return {data: {state: 20000, data: "以上で面接を終了します。"}}}); // 0.5秒後にテストデータを返す.
          // }
          // this.tmp += 1;
          // デバッグ用ここまで
          console.log(413, this.renderMessages);
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
            this.confirm()
          }
          //  if (response.data.state == 40400) {
          //   this.$router.push("/interview/user/login")
          //   this.$notify.warning({
          //     message: 'ログインが期限切れです,再度ログインしてください',
          //     type: 'warn'
          //   });
          // }
          else {
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
    confirm() {
      if (this.renderMessages.length == 2) {
        this.disabledByConfim = true
        this.flag = true
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
      this.$gtm.sendCustomEvent("show_personal_info");
    },
    logout() {
      // 清除本地存储中的 Token
      localStorage.removeItem("token"); // 或者使用 sessionStorage
      this.$gtm.sendCustomEvent("log_out");
      sessionStorage.removeItem("username");
      this.$router.push("/");
    },
    parseHTML: function (message) {
      return message.replace(/\n/g, "<br>"); // 将换行符替换为HTML的<br>标签
    },
    async sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    padZero(num) {
      return num < 10 ? '0' + num : num;
    },
    async generateDownloadLink() {
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
          return JSON.stringify({ role: role, content: rowData.text });
        }).join(',');
        const response = await MessageSend2({
          message: chatBody + ",{\"role\":\"user\", \"content\":\"CSVインポートしたいので、表を2つ出力してください。１つ目は面接での質問と回答を出力してください。必ず「質問項番」,「質問内容」,「回答」の3列にしてください。質問項番は1～10で順番に出力してください。質問内容は要約せずにそのまま出力してください。回答内容は要約せずにそのまま出力してください。２つ目は面接での評価を出力してください。必ず、「総合評価点」,「技術的スキル（0～40点）」,「コミュニケーション能力 (0-30点)」,「問題解決能力 (0-20点)」,「適応性と学習意欲 (0-10点)」,「評価理由」,「改善点」の7列にしてください。10個の回答を評価し、技術的スキル、コミュニケーション能力、問題解決能力 、適応性と学習意欲ごとに採点してください。総合評価点は各プログラム評価点の値の和とします。評価理由は4行程度で出力してください。改善点は4行程度で出力してください。CSV以外の出力はしないでください。CSVデータとそうではないところがわかるように、CSVデータは```で囲ってください。\" }"
        });
        console.log(647, response.data && response.data.state === 20000);
        // デバッグ用
        // let response; 
        // response = await this.sleep(500).then( () => {return {data: {state: 20000, data: "以下がCSVファイルです。\n```\n質問,回答\nほげ1,ふが1\n今日の朝ごはん,卵焼き\n```\n"}}}); // 0.5秒後にテストデータを返す.
        // デバッグ用ここまで
        if (response.data && response.data.state === 20000) {
          console.log(652, response);
          const csvData = response.data.data;
          const encodeData = new TextEncoder('utf-8').encode('\ufeff' + csvData);
          // console.log("encodeData : " + encodeData);
          const blob = new Blob([encodeData], { type: "text/csv;charset=utf-8" }); // CSVデータ作成

          const formData = new FormData(); // csvファイルbackendへ送信 
          formData.append('file', blob, csvFileName);
          const response1 = await MessageSend3(formData)
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
          localStorage.removeItem("token");
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
    getBreakpointConfig(breakpoint, isUserMessage) {
      // isUserMessageに応じて動的にspanとoffsetを変更するロジックを追加
      if (isUserMessage) {
        // ユーザーメッセージの場合
        return this.responsiveDesignSettings.userMessage[breakpoint];
      } else {
        // ボットメッセージの場合
        return this.responsiveDesignSettings.botMessage[breakpoint];
      }
    },
    // 音声入力試験用
    async startSpeechRecognition() {
      try {
        this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        const recognition = new webkitSpeechRecognition(); // 创建语音识别对象
        recognition.lang = 'ja-JP'; // 日本語に設定
        recognition.interimResults = false;
        recognition.start(); // 开始语音识别

        recognition.onresult = (event) => { // 当识别完成时
          this.userMessage = this.userMessage + event.results[0][0].transcript; // 将识别结果赋值给transcript变量
        }

        recognition.onend = () => {
          this.stopSpeechRecognition();
        }
      } catch (error) {
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
    renderMessages(newVal) {
      console.log(663, newVal.length);
      console.log(668, this.memberVo);

    },

    userMessage(newVal) {
      this.isInputNotEmpty = newVal.trim() !== "";
    },
    messages() {
      let addedMessage = this.messages.slice(-1)[0]; // 一番直近に追加されたメッセージ
      if (addedMessage.text.match(/以上で.*面接[はを]終了します/g)) {
        // 面接終了の処理記載
        // csvデータ作成 → ダウンロードリンク作成 → これ以上の質問を受け付けないようにする
        // CSVデータもChatGPTに作成してもらう。
        this.generateDownloadLink();
      }
    },
  },
  computed: {
    flag() {
      this.checkBoxes.map((item) => {
        this.flag = true
        if (item.checked == true) {
          return false;
        }
      })
    }
  },
  mounted: function () {
    // 契約者の会社名表示
    this.pageTitle = localStorage.getItem('contractor');
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
<style>
body,
html {
  background-color: #fae6f9 !important;
  font-family: "Arial", sans-serif;
  height: 100%;
  margin: 0;
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



.container {
  display: flex;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
  padding: 10px;
  margin-bottom: 10px;
}

.long-input {
  /* flex-grow: 1; */
  padding: 0;
  margin-right: 10px;
  margin-left: 160px;
  font-size: 16px;
  width: 60%;
  border-width: 2px;
  border-style: solid;
  border-color: #d3bce2;
  /* 设置边框样式为实线 */
}

.button-group {
  display: flex;
  gap: 10px;
}

.action-button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  height: 60px;
  width: 100px;
  background-color: rgb(0, 155, 99) !important;
  color: white !important;
}

.title {
  max-width: 90%;
  height: 3rem;
  line-height: 3rem;
  text-align: center;
  background-color: #92c2d8;
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
  margin-bottom: 40px;
  border: 1px solid #eaeaea;
  background-color: rgb(153, 255, 153) !important;
  color: #000000;
}

.user-message {
  background-color: #dddddd;
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

.footer>.el-col {
  justify-content: center;
  align-items: center;
  margin: 0;
  padding: 0;
}

.footer>.el-col:nth-child(1) {
  margin-right: 10px;
}

.footer>.el-col:nth-child(2) {
  margin-right: 5px;
}

.footer>.el-col:nth-child(3) {
  margin-left: 5px;
}

.footer>.el-col:nth-child(1),
.footer>.el-col:nth-child(3) {
  margin: 0 5px;
}

.custom-input-style .el-input__inner {
  width: 100%;
  border-radius: 30px;
  border-color: #d3bce2;
  color: #000000;
  min-height: 5px;
  height: 5px;
  margin: 15px 0 0;
  padding: 4px 10px;
  /* 调整内部填充以垂直居中文本 */
  overflow-y: hidden;
  /* 隐藏溢出的文本 */
  font-size: 16px;
  /* 根据需要调整字体大小 */
  line-height: 1.5;
  /* 根据需要调整行高 */
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

/* input[type="file"] {
  display: none;
} */

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

  .footer>.el-col {
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
    min-height: 3rem;
    /* 增加高度以便于触摸 */
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

@media only screen and (max-width: 600px) {
  .btn-spacing {
    margin-bottom: 60px;
    margin-top: 8px;
    white-space: nowrap;
    margin-right: 30px;
    padding-left: 0px;
    border-radius: 999px;
  }
}

@media only screen and (min-width: 601px) {
  .btn-spacing {
    margin-bottom: 10px;
    margin-top: 10px;
    white-space: nowrap;
    margin-right: 30px;
    padding-left: 0px;
    border-radius: 999px;
  }
}

.avatar {
  width: 100px;
  /* 调整头像宽度 */
  height: 100px;
  /* 调整头像高度 */
  border-radius: 50%;
  /* 设置圆角 */
}
</style>