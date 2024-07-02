import axios from "axios";
import store from "@/store";
import router from "@/router";
import { Message } from "element-ui";
const myAxios = axios.create({
    // baseURL: 'http://localhost:9999'
    baseURL: 'http://57.180.251.211:80'
})
// 定义请求拦截器
myAxios.interceptors.request.use(function (config) {
    // 为请求头挂载 Authorization 字段
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.token = token
    }
    return config
}, function (error) {
    return Promise.reject(error)
})

// 定义响应拦截器
myAxios.interceptors.response.use(function (response) {
    console.log(23, response);
    if (response.data.state != 20000) { Message.error(response.data.message) }

    return response
}, function (error) {
    console.log(28, error);
    Message.error("ログインが期限切れです,再度ログインしてください!")
    if (error.message == 'Network Error') {
        router.push('/manage-login')
    }
    return Promise.reject(error)

})

export default myAxios