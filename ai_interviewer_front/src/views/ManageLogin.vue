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
                    <el-button type="primary" @click="submitForm('ruleForm')" style="width: 200px;">ログイン</el-button>
                    <br>
                    <el-button type="warning" @click="resetForm('ruleForm')" style="width: 200px;">リセット</el-button>
                    <br>
                    <el-button type="success" @click="$router.push('/interview/user/login')"
                        style="width: 200px;">面接者ログイン</el-button>
                    <br>
                </el-form-item>
            </el-form>

        </div>
    </div>



</template>
<script>
import bgImg from '../assets/image.png'
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
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    let url = '/api/users/login';
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
                            this.$store.commit('initManageInfo', response.data.data)
                            console.log(token);
                            localStorage.setItem('token', token);
                            sessionStorage.setItem('username', this.ruleForm.username);
                            this.$gtm.sendLoginEvent(this.ruleForm.username);
                            const type = response.data.data.roleId
                            if (type === '2') {
                                this.$router.push('/manage-info')
                            } else if (type === '0') {
                                this.$router.push('/company-list')
                            }

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
        resetForm(formName) {
            this.$refs[formName].resetFields();
        },

        goTouserLogin() {
            // 使用 $router.push() 方法进行页面导航
            window.location.href = '/interview/user/login';
        }


    }
};
</script>


<style>
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
        background-size: contain;
        /* 调整背景图片显示方式 */
    }
}

@media only screen and (min-width: 601px) and (max-width: 1024px) {
    #background {
        background-image: url('../assets/image.png');
        background-size: contain;
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
