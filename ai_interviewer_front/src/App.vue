<template>
  <v-app>
    <v-app-bar app color="white" dark style="height: 55px;">
      <div class="d-flex align-center">

        <div class="box">
          <v-menu v-if="getPathFlag" offset-y>
            <template v-slot:activator="{ on, attrs }">
              <img src="./assets/menu.png" alt="Menu Icon" v-bind="attrs" v-on="on" 
                class="menu-icon" />
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

      <v-spacer></v-spacer>
      <img v-if="getChatViewFlag" :src="require('./assets/snsLogo.png')" alt="Logo" class="box__logo">
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
    getPathFlag() {
      const path = this.$route.path; 
      let pathFlag = true;
      if(path == '/interview/user/login'){
        pathFlag = false;
      }

      if(path == '/'){
        pathFlag = false;
      }
      if(path == '/chat'){
        pathFlag = false;
      }
      return pathFlag;
    },
    
    getChatViewFlag() {
      const path = this.$route.path; 
      let pathFlag = true;

      if(path == '/chat'){
        pathFlag = false;
      }
      return pathFlag;
    }
  },
  data: () => ({
    //
  }),
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
