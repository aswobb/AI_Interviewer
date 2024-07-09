<head>
    <!-- 其他头部内容 -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>

<template>
    <div id="background" class="login">
        <!-- test用 -->
        <div :style="textStyle">
            <h1 style="text-align: center; margin: 10px 0; color: #00FFFF; font-weight: bold;">管理者 ログイン</h1>
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
                <el-form-item label="ユーザー" prop="username">
                    <el-input v-model="ruleForm.username"></el-input>
                </el-form-item>
                <el-form-item label="パスワード" prop="password">
                    <el-input v-model="ruleForm.password" type="password"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('ruleForm')">ログイン</el-button>
                    <el-button @click="resetForm('ruleForm')">リセット</el-button>
                    <el-button type="primary" @click="goTouserLogin()">面接者登録へ</el-button>
                </el-form-item>
            </el-form>

        </div>
    </div>
</template>
<script>
import { Message } from 'element-ui';
import bgImg from '../assets/image.png'
import { loginAPI } from '@/api';
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
                    //////////
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

            // 使用 $router.push() 方法进行页面导航
            // window.location.href = '/interview/user/login';
            this.$router.replace({ path: '/interview/user/login' });

        }

    }
};
</script>

<style>
.custom-message .el-message__content {
    font-size: 20px;
}

body {
    background-color: #fae6f9 !important;
    background-position: center;
    /* 背景图片居中 */

}

.el-form-item__label {
    color: rgb(249, 242, 49) !important;
    /* 设置标签文字颜色为红色 */
    font-weight: bold;
    font-size: 15px;
}

.my-button {
    margin-top: 100px;
}
</style>
<style scoped>
.login-form {
    background-color: rgba(33, 110, 33, 0);
    width: 100%;
    /* 使用100%宽度，充满整个屏幕 */
    box-sizing: border-box;
    /* 使padding不会撑大宽度 */
    padding: 20px;
    /* 减小padding，以适应小屏幕 */
    margin: 0;
    /* 取消上下边距，以充分利用空间 */

}

@media only screen and (min-width: 1025px) {
    #background {
        background-Image: url('../assets/image.png');
        background-size: cover;
        /* 调整背景图片显示方式 */
    }
}

@media only screen and (max-width: 600px) {
    #background {
        background-Image: url('../assets/image.png');
        background-size: cover;
        background-size: (100% 100%);
        /* 调整背景图片显示方式 */
    }
}

@media only screen and (min-width: 601px) and (max-width: 1024px) {
    #background {
        background-image: url('../assets/image.png');
        background-size: (100% 100%);
        background-size: cover;
    }
}







/* 如果需要保持在大屏幕上的一些样式，可以使用媒体查询 */
@media only screen and (min-width: 600px) {
    .login-form {
        width: 500px;
        /* 在大屏幕上保持原有宽度 */
        margin: 50px auto;
        /* 保持上下居中 */
        padding: 30px 50px;
        padding-top: 100px;
    }
}

h1 {
    text-align: center;
    margin: 10px 0;
    font-size: 1.7em;
}

.login {
    display: flex;
    justify-content: center;
    /* 水平居中 */
    align-items: center;
    /* 垂直居中 */
    width: 100vw;
    height: 100vh;
    /* 容器的高度 */
}
</style>