import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';
Vue.use(Vuex)
export default new Vuex.Store({
  state: {
    //管理者信息
    manageInfo: [],
    //面试者信息
    interviewerInfo: [],
    interviewerInfo: [],
    //总页数
    totalItems: null,
    //会社管理信息
    companyInfo: [],
    companyInfo: [],
    //公司总数
    companyTotalItems: null

  },
  getters: {
  },
  mutations: {
    initManageInfo(state, data) {

      state.manageInfo = data
      console.log(26, state.manageInfo);
    },
    cleanCache(state) {
      state.manageInfo = []
      localStorage.setItem('token', '');
      state.interviewerInfo = []
      state.companyInfo = []
    },
    initInterviewerInfo(state, data) {
      state.interviewerInfo = data.data.records
      state.totalItems = data.data.total
    },
    initCompanyInfo(state, data) {
      state.companyInfo = data.data.records
      state.companyTotalItems = data.data.total
    },
  },
  actions: {

  },
  modules: {
  },
  plugins: [createPersistedState()]
})
// 刷新同步