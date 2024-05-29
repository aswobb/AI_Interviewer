import Vue from 'vue'
import VueRouter from 'vue-router'
import LoginView from '../views/LoginView.vue'
import ManageView from '../views/SNSsoftmanage.vue'
import { Message } from 'element-ui'
Vue.use(VueRouter)

const routes = [
    {
        path: '/about',
        component: () => import('../views/AboutView.vue')
    },
    {
        path: '/chat',
        component: () => import('../views/ChatView.vue'),
        name: 'ChatApp',
    },
    {
        path: '/',
        component: () => import('../views/ManageLogin.vue')

    },
    {
        path: '/manage',
        component: () => import('../views/SNSsoftmanage.vue')

    },
    {
        path: '/accountset',
        component: () => import('../views/accountset.vue')

    },
    {
        path: '/manage-login',
        component: () => import('../views/ManageLogin.vue')

    },
    {
        path: '/company-list',
        component: () => import('../views/CompanyList.vue')

    },
    {
        path: '/interview-list',
        component: () => import('../views/InterviewList.vue')

    },
    {
        path: '/manage-info',
        component: () => import('../views/ManageInfo.vue')

    },
    {
        path: '/usermanage',
        component: () => import('../views/manage.vue')

    },
    {
        path: '/setpassword',
        component: () => import('../views/setpassword.vue')

    },
    {
        path: '/interview/user/login',
        component: () => import('../views/UserLoginView.vue')

    }



]
const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

//定义白名单
let writeList = ['/manage-login', '/interview/user/login']
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    console.log(83, token);
    if (token && token !== null && token !== '') {
        console.log(1);
        next()
        localStorage.setItem('token', token);
    } else {
        console.log(88, to.path);
        if (writeList.includes(to.path)) {
            console.log("跳转登录");
            next()
            localStorage.setItem('token', null);
        } else {
            next('/interview/user/login')
            Message.warning({
                message: '先にログインしてください',
                type: 'warn'
            });
        }
    }
}
)
export default router
