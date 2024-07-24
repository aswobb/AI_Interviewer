<template>
    <div class="custom">
        <div class="user">
            <div class="login-form">
                <header class="user__header">
                    <img class="menu-icon" src="../assets/bot.png" alt="" />
                    <h1 class="user__title"></h1>
                </header>
                <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px">
                    <el-form-item  prop="username">
                        <div class="custom-form-item">
                            <label class="custom-form-item-lable"> ユーザー</label>
                            <el-input v-model="ruleForm.username"></el-input>
                        </div>
                    </el-form-item>
                    <el-form-item  prop="password">
                        <div class="custom-form-item">
                            <label class="custom-form-item-lable"> パスワード</label>
                            <el-input v-model="ruleForm.password" type="password"></el-input>
                        </div>
                    </el-form-item>
                    <div class="button-container">
                        <el-button type="primary" @click="submitForm('ruleForm')">ログイン</el-button>
                        <el-button @click="resetForm('ruleForm')">リセット</el-button>
                        <el-button type="primary" @click="goTouserLogin()">面接者登録へ</el-button>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>
<script>
import bgImg from '../assets/image.png'
import { loginAPI } from '@/api';
export default {
    data() {
        return {
            bgImg: bgImg,
            isDesktop: window.innerWidth > 600,
            ruleForm: {
                username: '',
                password: ''
            },
            rules: {
                username: [
                    { required: true, message: 'ユーザー名を入力してください', trigger: 'blur' },
                    { min: 2, max: 15, message: '文字数は2から15文字まで', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: 'パスワードを入力してください', trigger: 'blur' },
                    { min: 4, max: 15, message: '文字数は4から15文字まで', trigger: 'blur' }
                ]
            }
        };
    },
    mounted() {
        window.addEventListener('resize', this.updateIsDesktop);
    },
    methods: {
        updateIsDesktop() {
            this.isDesktop = window.innerWidth > 600;
        },
        submitForm(formName) {
            this.$refs[formName].validate(async (valid) => {
                if (valid) {
                    const response = await loginAPI(this.ruleForm)
                    if (response.data.state == 20000) {
                        this.$message({
                            message: 'ログインが成功しました.',
                            type: 'success'
                        });
                        const token = response.data.data.token;
                        this.$store.commit('initManageInfo', response.data.data)
                        localStorage.setItem('token', token);
                        const type = response.data.data.roleId
                        if (type === '2') {
                            this.$router.push('/manage-info')
                        } else if (type === '0') {
                            this.$router.push('/company-list')
                        }
                    }
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        },

        goTouserLogin() {
            this.$router.replace({ path: '/interview/user/login' });
        }
    }
};
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
    margin-left: 36px;
    margin-top: 40px;
    .el-button{
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