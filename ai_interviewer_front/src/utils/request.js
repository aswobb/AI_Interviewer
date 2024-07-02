import axios from "axios";
import store from "@/store";
import router from "@/router";
import { Message } from "element-ui";
const myAxios = axios.create({
    // baseURL: 'http://localhost:9999'
    baseURL: 'http://57.180.251.211:80/api'
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
    // 响应状态码为 2xx 时触发成功的回调，形参中的 response 是“成功的结果”
    console.log(23, response);
    return response
}, function (error) {

    console.log(26, error);
 
  return Promise.reject(error)

})

export default myAxios