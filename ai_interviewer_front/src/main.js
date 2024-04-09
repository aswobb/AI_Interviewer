
import Vue from 'vue'

import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from "axios";
import qs from "qs";

import VueGtm from 'vue-gtm'
import GtmDataLayer from './plugins/gtm-datalayer'
import Vuetify from './plugins/vuetify'
import QBtn from 'quasar'
import QTable from 'quasar'
import QPage from 'quasar'
import QPageContainer from 'quasar'
import QLayout from 'quasar'
import QIcon from 'quasar'
import 'quasar/dist/quasar.css'

Vue.use(QBtn)
Vue.use(QTable)
Vue.use(QPage)
Vue.use(QPageContainer)
Vue.use(QLayout)
Vue.use(QIcon)
Vue.use(VueGtm, {
  id: '' // AI面接官用のGoogle Tag払い出したら対応
})
Vue.use(GtmDataLayer)

Vue.use(Vuetify)
Vue.use(ElementUI)
Vue.prototype.axios = axios
Vue.prototype.qs = qs
Vue.config.productionTip = false


// 引入API接口文件
import global_ from './components/global'//引用文件

import vuetify from './plugins/vuetify'
Vue.prototype.GLOBAL = global_//挂载到Vue实例上面




new Vue({
  router,
  store,
  render: h => h(App),
  vuetify,

  components: {
    QBtn, QTable, QPage, QPageContainer, QLayout, QIcon
  }
}).$mount('#app')
