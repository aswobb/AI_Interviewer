<template>
    <div class="custom">
        <div class="user">
            <div class="login-form">
                <header class="user__header">
                    <img class="menu-icon" src="../assets/bot.png" alt="" />
                    <h1 class="user__title"></h1>
                </header>
                <el-form :model="ruleForm" :rules="rules" ref="ruleForm"  label-width="100px">
                    <el-form-item prop="interviewerId">
                        <div class="custom-form-item">
                            <label class="custom-form-item-lable"> 面接ID</label>
                            <el-input style="width: 250px;" v-model="ruleForm.interviewerId"></el-input>
                        </div>
                    </el-form-item>
                    <el-form-item prop="interviewerName">

                        <div class="custom-form-item">
                            <label class="custom-form-item-lable"> 面接者氏名</label>
                            <el-input style="width: 250px;" v-model="ruleForm.interviewerName" type="username"></el-input>
                        </div>
                    </el-form-item>
                    <div class="button-container">
                        <el-form-item>
                            <el-button type="primary" @click="onSubmit('ruleForm')">ログイン</el-button>
                            <el-button @click="resetForm()">リセット</el-button>
                        </el-form-item>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>
<script>
import bgImg from './bot-avatar.png';
import { interviewerLogin } from '@/api'
export default {
    data() {
        return {
            textStyle: {
                fontWeight: 'bold', // 粗体
                color: '#00FFFF', // 黑色
                backgroundColor: 'rgba(0, 0, 0, 0.4)',
                paddingRight: '20px',
                paddingLeft: '10px',
                // 如果需要其他样式，可以继续添加
            },
            ruleForm: {
                interviewerId: '',
                interviewerName: ''
            },
            rules: {
                interviewerId: [
                    { required: true, message: '面接IDを入力してください', trigger: 'blur' }
                ],
                interviewerName: [
                    { required: true, message: '面接者氏名を入力してください', trigger: 'blur' }
                ]
            }

        }
    },
    methods: {
        onSubmit(form) {
            this.$refs.ruleForm.validate().then(async success => {
                if (success) {
                    var localPath = this.GLOBAL.localSrc;
                    const response = await interviewerLogin(this.ruleForm)
                    if (response.data.state == 20000) {
                        console.log('ログインが成功しました.');
                        this.$message({
                            message: 'ログインが成功しました.',
                            type: 'success'
                        });
                        console.log(75, response.data.data.InterviewInfo.contractor);
                        const token = response.data.data.InterviewInfo.token;
                        this.$store.state.contractor = response.data.data.InterviewInfo.contractor;
                        localStorage.setItem('token', token);
                        localStorage.setItem('contractor', this.$store.state.contractor);
                        this.$store.state.companyMemberInfo = response.data.data.memberInfo
                        sessionStorage.setItem('username', this.ruleForm.interviewerId);
                        this.$gtm.sendLoginEvent(this.ruleForm.interviewerId); // ログインイベント送出
                        this.$router.push({ name: 'ChatApp' })
                    }
                }
            });
        },
        resetForm() {
            this.ruleForm.interviewerId = '';
            this.ruleForm.interviewerName = '';
        },
    }
}
</script>

<style scoped lang="scss">
$font-family: "Roboto";
$font-size: 25px;
$color-primary: #ABA194;
$white: rgba(255, 255, 255, 0.2);

a,
input:focus,
select:focus,
textarea:focus,
button:focus {
    outline: none;
}

.custom {
    margin: 0px;
    padding: 1px;
    box-sizing: border-box;
    font-family: $font-family;
    font-size: $font-size;
    background-size: 200% 100% !important;
    animation: move 6s ease infinite;
    transform: translate3d(0, 0, 0);
    background: linear-gradient(45deg, #49D49D 10%, #adb0d2 90%);
    height: 100vh;
}

.user {
    width: 100%;
    max-width: 553px;
    margin: 10vh auto;
}

.user__header {
    text-align: center;
    opacity: 0;
    transform: translate3d(0, 500px, 0);
    animation: arrive 500ms ease-in-out 0.7s forwards;
    margin-top: -35px;
}

.user__title {
    font-size: $font-size;
    font-weight: 500;
    color: white;
}

.form {
    margin-top: 40px;
    border-radius: 6px;
    overflow: hidden;
    opacity: 0;
    transform: translate3d(0, 500px, 0);
    animation: arrive 500ms ease-in-out 0.9s forwards;
}

.form--no {
    animation: NO 1s ease-in-out;
    opacity: 1;
    transform: translate3d(0, 0, 0);
}

.form__input {
    display: block;
    width: 100%;
    padding: 20px;
    font-family: $font-family;
    -webkit-appearance: none;
    border: 0;
    outline: 0;
    transition: 0.3s;

    &:focus {
        background: darken(#fff, 3%);
    }
}

.btn {
    display: block;
    width: 100%;
    padding: 20px;
    font-family: $font-family;
    -webkit-appearance: none;
    outline: 0;
    border: 0;
    color: white;
    background: $color-primary;
    transition: 0.3s;

    &:hover {
        background: darken($color-primary, 5%);
    }
}

@keyframes NO {

    from,
    to {
        -webkit-transform: translate3d(0, 0, 0);
        transform: translate3d(0, 0, 0);
    }

    10%,
    30%,
    50%,
    70%,
    90% {
        -webkit-transform: translate3d(-10px, 0, 0);
        transform: translate3d(-10px, 0, 0);
    }

    20%,
    40%,
    60%,
    80% {
        -webkit-transform: translate3d(10px, 0, 0);
        transform: translate3d(10px, 0, 0);
    }
}

@keyframes arrive {
    0% {
        opacity: 0;
        transform: translate3d(0, 50px, 0);
    }

    100% {
        opacity: 1;
        transform: translate3d(0, 0, 0);
    }
}

@keyframes move {
    0% {
        background-position: 0 0;
    }

    50% {
        background-position: 100% 0;
    }

    100% {
        background-position: 0 0;
    }
}

.menu-icon {
    cursor: pointer;
    width: 80px;
    height: 80px;
}

.login-form {
    padding-top: 35px;
    background: rgba(255, 255, 255, 0.13);
    /* 半透明白色背景 */
    padding: 3em;
    height: 100%;
    width: 100%;
    border-radius: 76px;
    box-shadow: 20px 20px 40px -6px rgba(0, 0, 0, 0.2);
    text-align: center;
    position: relative;
    transition: all 0.2s ease-in-out;
    z-index: 1;
}

.button-container {
    display: flex;
    gap: 10px;
    justify-content: flex-start;
    width: 100%;
    margin-left: 8px;
    margin-top: 40px;

    .el-button {
        border-radius: 76px;
    }
}

.custom-form-item {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin-right: 84px;
}

.custom-form-item-lable{
  text-align: left;
  width: 100%;
  margin-bottom: 8px;
  padding: 0;
}

</style>