import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    manageInfo: {}
  },
  getters: {
  },
  mutations: {
    initManageInfo(state, data) {
      state.manageInfo = data
      console.log(state.manageInfo);
    }
  },
  actions: {

  },
  modules: {
  }
})
