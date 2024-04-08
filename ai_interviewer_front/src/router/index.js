import Vue from 'vue'
import VueRouter from 'vue-router'
import LoginView from '../views/LoginView.vue'
import ManageView from '../views/SNSsoftmanage.vue'

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
        component: LoginView
        
    },
    {
        path: '/manage',
        component: ManageView

    },
    {
        path: '/accountset',
        component: () => import('../views/accountset.vue')

    },
    {
        path: '/usermanage',
        component: () => import('../views/manage.vue')

    },
    {
        path: '/setpassword',
        component: () => import('../views/setpassword.vue')

    }

    
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router
