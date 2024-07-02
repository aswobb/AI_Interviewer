const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: [
    'vuetify'
  ],
  transpileDependencies: true,
  // 添加此行代码
  lintOnSave: false,
  devServer: {
    client: {
      overlay: false
    }
  }
})