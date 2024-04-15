import InterviewList from '@/views/InterviewList.vue'
import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    //管理者信息
    manageInfo: {},
    //面试者信息
    interviewerInfo: {},
    //总页数
    totalItems: null
  },
  getters: {
  },
  mutations: {
    initManageInfo(state, data) {
      state.manageInfo = data
      console.log(state.manageInfo);
    },
    initInterviewerInfo(state, data) {
      state.interviewerInfo = data
      console.log(state.interviewerInfo);
    }
  },
  actions: {

  },
  modules: {
  },
  plugins: [createPersistedState()]
})
