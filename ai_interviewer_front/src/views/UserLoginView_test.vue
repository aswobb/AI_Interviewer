<head>
    <!-- 其他头部内容 -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<template>
    <QLayout>
      <QPageContainer>
        <q-page id="background" class="flex flex-center">
        <div class="q-pa-md" style="width: 350px; max-width: 90vw;">
          <div class="text-h4 text-center q-mb-md" style="color: #007bff; font-weight: bold;">AI面接官ログイン</div>
          <q-form ref="form" @submit="onSubmit" @reset="onReset" class="q-gutter-md">
            <q-input filled v-model="ruleForm.interviewerId" label="面接ID" lazy-rules :rules="[val => val && val.length > 0 || '面接IDを入力してください']"style="background-color: #fff;height: 50px;"/>
            <q-input filled type="password" v-model="ruleForm.interviewerName" label="パスワード" lazy-rules :rules="[val => val && val.length > 0 || '面接者氏名を入力してください']"style="background-color: #fff;height: 50px;"/>
            <div>
              <q-btn label="ログイン" type="submit" color="primary" class="full-width q-mb-sm"/>
              <q-btn label="リセット" type="reset" color="primary" class="full-width q-mb-sm"/>
            </div>
          </q-form>
        </div>
      </q-page>
    </QPageContainer>
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
        onSubmit(form) {
        this.$refs.form.validate().then(success => {
        if (success) {
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
#background {
  background-image: url('./bot-avatar.png');
  background-size: cover;
}

/* 小屏幕下的背景图片 */
@media only screen and (max-width: 600px) {
  #background {
    background-image: url('./bot-avatar.png');
    background-size: contain;
  }
}

/* 中等屏幕下的背景图片 */
@media only screen and (min-width: 601px) and (max-width: 1024px) {
  #background {
    background-image: url('./bot-avatar.png');
  }
}

/* 大屏幕下的背景图片 */
@media only screen and (min-width: 1025px) {
  #background {
    background-image: url('./bot-avatar.png');
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