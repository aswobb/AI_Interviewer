
<template>
    <QLayout>
      <q-page-container>
        <q-page class="welcome-page">
  
          <div class="welcome-content">
            <h3>パスワード変更</h3>
              <q-btn @click="goToDashboard" color="primary" label="ログイン画面へ" :style="{ width: '140px', height: '50px' }"class="btn-spacing"/>
              <q-input
                v-model="inputValue1"
                label="現パスワード"
                type="password"
                outlined
                
                class="input-margin"/>
                <q-input
                v-model="inputValue2"
                label="新パスワード"
                type="password"
                outlined
                
                class="input-margin">
                </q-input>

                <q-input
                v-model="inputValue4"
                label="新パスワード再入力"
                type="password"
                outlined
                
                class="input-margin"/>


                <q-btn @click="showDialog" color="primary" label="確認" :style="{ width: '140px', height: '50px' }"class="btn-spacing"/>



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
      updateSelectedItems(value) {
      // 将所有复选框的选择状态设置为 false
      for (const key in this.selectedItems) {
        if (key !== value) {
          this.selectedItems[key] = false;
        }
      }
    },

    showDialog() {
      this.$q.dialog({
        title: '確認',
        message: '以上の内容で登録してよろしいでしょうか？',

        ok: {
          push:true,
          label: '確定',
          color: 'primary',
          class:'custom-btn-style'
        },

        cancel: {
          label: '取消',
          color: 'negative',
          class:'custom-btn-style'
        },

        style: {
          display: 'flex',
          flexDirection: 'column', // 将按钮放置在对话框的底部
          weigh:'380px',
          height:'200px'



        }
        
      })
    },
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword; // 切换密码显示状态
    }


    },

    data() {
    return {
      inputValue1: '',
      inputValue2: '',
      inputValue3: '',
      inputValue4: '',
      items: [
      {lebal:'ベーシック',value:'ベーシック'}, 
      {lebal:'スタンダード',value:'スタンダード'}, 
      {lebal:'プレミアム',value:'プレミアム'}
      ], // 示例项目列表
      selectedItems: {
        'ベーシック': false,
        'スタンダード': false,
        'プレミアム': false
      },
      password: '', // 存储密码值
      showPassword: false // 控制密码显示状态
    }
    
  }
  }
  </script>
  
  <style scoped>
  .welcome-page {
    /* 设置页面背景颜色 */
    background-color: #1dcece;
  
    /* 设置页面高度为视口高度 */
    height: 100vh;
  
    /* 设置页面内容垂直居中 */
    display: flex;
    justify-content: center;
    text-align: center;
  
    /* 设置页面内边距 */
    padding: 1px;

    align-items: left;
  }
  
  .welcome-content {
    text-align: center;
  }
  
  .btn-spacing {
    margin-right: 15px; /* 设置按钮之间的右边距 */
    margin-top: 40px;
  }

  .input-margin {
  padding-top: 30px; /* 增加顶部内边距 */
  padding-bottom: 5px; /* 增加底部内边距 */
  font-size: 25px;
  min-width: 75%;

  line-height:normal ;

}
.custom-btn-style {
  width: 200px;
  height: 100px;
  font-size: 18px;
  margin-left:100px;
}
.q-checkbox__inner {
  min-height: 1px; /* 设置复选框的最小高度 */
  text-align: left;
}
.select{
  text-align: left;
  align-items: left;
}

.checkbox-container {
  display: flex; /* 使用 Flexbox 布局 */
  flex-direction: column; /* 设置纵向排列 */
  margin-bottom: 10px; /* 设置多选框之间的垂直间距 */
  text-align: left;
  align-items: left;
}

.checkbox {
  display: flex; /* 设置复选框按钮内部元素为 Flexbox 布局 */
  align-items: left; /* 设置垂直居中对齐 */
}

.label {
  margin-left: 8px; /* 设置标签文字与复选框之间的左边距 */
}


  </style>
  