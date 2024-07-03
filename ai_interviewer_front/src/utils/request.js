import axios from "axios";
import store from "@/store";
import router from "@/router";
import { Message } from "element-ui";
const myAxios = axios.create({
    baseURL: 'http://localhost:9999'
    // baseURL: 'https://ainterviewer.jp/api'
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
    let errorCode = [40000, 40001, 40009, 40002, 40003, 40004, 40005, 40400, 40401, 40402, 40900, 50000, 50001, 50002, 50003, 50401, 60001, 70001]
    if (errorCode.includes(response.data.state)) {
        Message.error(response.data.message)
    }

    return response
}, function (error) {
    if (error.response.status == 401) {
        router.push('/manage-login')
        Message.warning("ログインが期限切れです,再度ログインしてください!")
    }
    return Promise.reject(error)

})
export default myAxios