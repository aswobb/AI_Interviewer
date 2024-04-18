const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: [
    'vuetify'
  ]
})

module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://192.168.1.160:9999',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '',  // 将 /api 前缀重写为空，即去掉 /api
        },
      },
    },
  },

  transpileDependencies: [
    'vuetify'
  ]
};
//lihao测试
