<template>
  <v-app>
    <v-app-bar app color="white" dark style="height: 55px;">
      <div class="d-flex align-center">

        <div class="box">
          <v-menu v-if="getPathFlag" offset-y>
            <template v-slot:activator="{ on, attrs }">
              <img src="./assets/menu.png" alt="Menu Icon" v-bind="attrs" v-on="on" class="menu-icon" />
            </template>

            <v-list>
              <v-list-item @click="navigateToManageInfo">
                <v-list-item-title>管理者情報</v-list-item-title>
              </v-list-item>
              <v-list-item @click="navigateToInterview">
                <v-list-item-title>面接情報</v-list-item-title>
              </v-list-item>
              <v-list-item @click="navigateToCompanymember">
                <v-list-item-title>会社員情報</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>


      </div>
      <div class="ellipse-box" v-show="isChatPage">
        <span class="ellipse-text">{{ getCompanyName }}</span>
      </div>
      <v-spacer></v-spacer>
      <img v-show="!isChatPage" :src="require('./assets/snsLogo.png')" alt="Logo" class="box__logo">
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script>

export default {
  name: 'App',
  created() {
    this.user = this.$store.state.manageInfo


  },
  computed: {
    getCompanyName() {
      let companyName = null
      if (this.$store.state.contractor != null) {
        companyName = this.$store.state.contractor
      }
      return companyName
    },
    getPathFlag() {
      const path = this.$route.path;
      let pathFlag = true;
      if (path == '/interview/user/login') {
        pathFlag = false;
      }

      if (path == '/') {
        pathFlag = false;
      }

      if (path == '/chat') {
        pathFlag = false;
      }
      if (path == '/manage-login') {
        pathFlag = false;
      }
      return pathFlag;
    },
    isChatPage() {
      let chatFlag = false
      const path = this.$route.path;
      if (path == '/chat') {
        chatFlag = true;
      }
      return chatFlag
    }
  },
  data() {
    return {
      companyNmae: null
    }
  },
  methods: {
    navigateToManageInfo() {
      this.$router.push({
        path: '/manage-info'
      });
    },
    navigateToCompanymember() {
      this.$router.push({
        path: '/companymember',
        query: { id: this.user.id },
      });
    },
    navigateToInterview() {
      this.$router.push({
        path: '/interview-list',
        query: { id: this.user.id },
      });
    },
  },
};
</script>

<style>
.ellipse-box {
  margin-left: 10%;
  left: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80%;
  /* 根据需要调整宽度 */
  height: 40px;
  /* 根据需要调整高度 */
  border-radius: 50px;
  /* 椭圆形的关键属性 */
  background-color: #4CAF50;
  /* 背景颜色 */
  color: white;
  /* 文字颜色 */
  font-size: 18px;
  /* 文字大小 */
  text-align: center;
}

.ellipse-text {
  font-weight: bold;
  /* 文字加粗 */
}

.box {
  display: flex;
  justify-content: center;
  /* 文本水平居中 */
  align-items: center;
  /* 文本垂直居中 */
  text-align: center;
  /* 文本水平居中 */
  height: 80px;
  padding-top: 2px;
}

.box__logo {
  width: 120px;
  height: 30px;
  margin-right: 10px;
}

.menu-icon {
  cursor: pointer;
  width: 24px;
  height: 24px;
}
</style>
