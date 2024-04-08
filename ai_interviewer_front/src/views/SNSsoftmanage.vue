
<template>
  <QLayout>
    <q-page-container>
      <q-page class="welcome-page">

        <div class="welcome-content">
          <h3>SNSソフト管理用画面</h3>
          
            <q-btn @click="goToDashboard" color="primary" label="ログイン画面へ" :style="{ width: '140px', height: '50px' }"class="btn-spacing"/>
            <q-btn @click="goToaccountset" color="primary" label="アカウント登録・変更" :style="{ width: '140px', height: '50px' }"class="btn-spacing"/>


            <p class="user-list">ユーザー一覧</p>
            <div class="text-container">
            DBから取ったユーザーデータ
            </div>
        </div>
    
      </q-page>
    </q-page-container>
  </QLayout>
</template>

<script>

export default {
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
      }
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
  margin-right: 25px; /* 设置按钮之间的右边距 */
  margin-bottom: 20px;
  margin-top:10px;

  text-align: center;
}

.text-container {
  width: 1500px; /* 设置文本容器的宽度 */
  height: 1000px; /* 设置文本容器的高度 */
  padding: 20px; /* 设置文本容器的内边距 */
  border: 1px solid #ccc; /* 添加边框 */
  background-color: #f0f0f0; /* 设置背景颜色 */
}
.user-list {
  font-size: 20px; /* 设置文字大小为 20 像素 */
}
</style>
