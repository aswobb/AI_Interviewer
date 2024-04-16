<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <div class="d-flex align-center">
        <v-img alt="Vuetify Logo" class="shrink mr-2" contain :src="require('@/assets/SNS.jpg')"
          transition="scale-transition" width="40" />

        <div class="box">

          <h5 style="font-style: italic;color: black">SNS SOFT AI面接官</h5>

        </div>


      </div>

      <v-spacer></v-spacer>
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
  height: 400px;
  padding-top: 2px;
}
</style>
