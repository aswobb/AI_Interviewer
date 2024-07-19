<template>
  <QLayout>
    <q-page-container>
      <q-page class="welcome-page">

        <div class="welcome-content">
          <h3>管理用画面</h3>

          <q-btn @click="goToDashboard" color="primary" label="ログイン画面へ" :style="{ width: '140px', height: '50px' }"
            class="btn-spacing" />
          <q-btn @click="goTopasswordset" color="primary" label="パスワード再設定" :style="{ width: '140px', height: '50px' }"
            class="btn-spacing" />
          <q-btn @click="" color="primary" label="面接情報一覧" :style="{ width: '140px', height: '50px' }"
            class="btn-spacing" />


          <div>
            <q-input v-model="username" class="font" readonly />
            <q-input v-model="course" class="font" readonly />
            <q-input v-model="usedinfo" class="font" readonly />
            <q-input v-model="timescanuse" class="font" readonly />

          </div>
        </div>

      </q-page>
    </q-page-container>
  </QLayout>
</template>

<script>

export default {
  data() {
    return {
      // DBから取得されたデータに変更
      username: '契約者：' + 'データベースから取得した契約者名',
      course: '契約コース：' + 'データベースから取得した契約コース',
      usedinfo: '利用実績：' + 'データベースから取得した利用実績',
      timescanuse: '残利用回数：' + 'データベースから取得した残利用回数'
    }
  },
  methods: {
    goToDashboard() {
      // 在此处实现导航到仪表盘页面的逻辑
      // 例如：使用路由导航
      this.$router.push('/')
    },
    goToaccountset() {
      // 在此处实现导航到仪表盘页面的逻辑
      // 例如：使用路由导航
      this.$router.push('/accountset')
    },
    goTopasswordset() {
      // 在此处实现导航到仪表盘页面的逻辑
      // 例如：使用路由导航
      this.$router.push('/setpassword')
    },
  }
}
</script>

<style scoped>
.welcome-page {
  /* 设置页面背景颜色 */
  background-color: #b7ee1f;

  /* 设置页面高度为视口高度 */
  height: 100%;

  /* 设置页面内容垂直居中 */
  display: flex;
  justify-content: center;

  /* 设置页面内边距 */
  padding: 2px;
}

.welcome-content {
  text-align: center;
}

.btn-spacing {
  margin-right: 25px;
  /* 设置按钮之间的右边距 */
  margin-bottom: 20px;
  margin-top: 10px;

  text-align: center;
}

.user-list {
  font-size: 20px;
  /* 设置文字大小为 20 像素 */

}

.font {
  font-size: 20px;
  margin-top: 10px;
}
</style>