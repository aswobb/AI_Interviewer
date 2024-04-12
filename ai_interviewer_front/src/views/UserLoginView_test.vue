<head>
    <!-- 其他头部内容 -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<template>
    <QLayout>
        <q-page class="flex flex-center" :style="{ backgroundImage: `url(${bgImg})`, backgroundSize: 'cover'}">
        <div class="q-pa-md" style="width: 350px; max-width: 90vw;">
          <div class="text-h4 text-center q-mb-md" style="color: #FFFF00;font-weight: bold;">ログイン</div>
          <q-form @submit="onSubmit" @reset="onReset" class="transparent-form q-gutter-md">
            <q-input filled v-model="ruleForm.interviewerId" label="面接ID" lazy-rules :rules="[val => val && val.length > 0 || '面接IDを入力してください']"/>
            <q-input filled type="password" v-model="ruleForm.interviewerName" label="パスワード" lazy-rules :rules="[val => val && val.length > 0 || '面接者氏名を入力してください']"/>
            <div>
              <q-btn label="ログイン" type="submit" color="primary" class="full-width q-mb-sm"/>
              <q-btn label="リセット" type="reset" color="primary" class="full-width q-mb-sm"/>
            </div>
          </q-form>
        </div>
      </q-page>
    </QLayout>
  </template>
     
    <script>
    import bgImg from './bot-avatar.png';
    export default {
      data() {
        return {
            ruleForm: {
        interviewerId: '',
        interviewerName: ''
      },
       bgImg:bgImg

        }
      },
      methods: {
        onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          var localPath = this.GLOBAL.localSrc;
          let url = '/api/users/interviewerLoginInfo';
          console.log('尝试登录')
          console.log('请求路径为:' + url)
          console.log('请求参数为:' + this.ruleForm)
          console.log(this.ruleForm)
          // let dateFrom = this.qs.stringify(this.ruleForm)
          // 设置请求头为 application/json
          let config = {
            headers: {
              'Content-Type': 'application/json'
            }
          };
          this.axios.post(url, this.ruleForm, config).then((response) => {
            console.log(response);
            if (response.data.state == 20000) {
              console.log('ログインが成功しました.');
              this.$message({
                message: 'ログインが成功しました.',
                type: 'success'
              });

              const token = response.data.data.token;
              const contractor = response.data.data.contractor;
              localStorage.setItem('token', token);
              localStorage.setItem('contractor', contractor);

              sessionStorage.setItem('username', this.ruleForm.interviewerId);
              this.$gtm.sendLoginEvent(this.ruleForm.interviewerId); // ログインイベント送出
              this.$router.push({name: 'ChatApp'})
            } else {
              console.log('ログインに失敗しました。ユーザー名またはパスワードが正しくありません.')
              this.$notify.error({
                title: 'ログインに失敗しました.',
                message: 'ログインに失敗しました。ユーザー名またはパスワードが正しくありません.'
              });
            }
          });
        } else {
          console.log('error submit!!')
          return false;
        }
      });
    },
        onReset() {
          this.ruleForm.interviewerId = '';
          this.ruleForm.interviewerName = '';
        }
      }
    }
    </script>
     
    <style>
    @media only screen and (min-width: 768px){
    .background-image {
        background-image: url('./bot-avatar.png') !important;
        background-size:cover !important; /* 可选，将背景图片缩放以填充整个屏幕 */
        /* background-position: center;  */

        }
    }

    @media only screen and (max-width: 767px) {
    .background-image  {
        background-image: url('./bot-avatar.png') !important;
        background-size: contain !important; /* 或 contain */
        }
    }

    .transparent-form {
        background-color: rgba(255, 255, 255, 0.9);
    }  



    .q-page {
      background-color: #f5f5f5;
      min-height: 100vh;
    }

    .login {
        height: 700px;
    }





    </style>