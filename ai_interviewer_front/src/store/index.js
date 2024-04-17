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
    },
    initInterviewerInfo(state, data) {
      state.interviewerInfo = data.data.records
      state.totalItems = data.data.total
    }
  },
  actions: {

  },
  modules: {
  },
  plugins: [createPersistedState()]
})
