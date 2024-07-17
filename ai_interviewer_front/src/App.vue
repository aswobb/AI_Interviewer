<template>
  <v-app>
    <v-app-bar app color="white" dark style="height: 55px;">
      <div class="d-flex align-center">

        <div class="box">

          <h5 style="font-style: italic;color: black">AI面接官</h5>

        </div>


      </div>

      <v-spacer></v-spacer>
      <img :src="require('./assets/snsLogo.png')" alt="Logo" class="box__logo">
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script>

export default {
  name: 'App',

  data: () => ({
    //
  }),
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
</style>
