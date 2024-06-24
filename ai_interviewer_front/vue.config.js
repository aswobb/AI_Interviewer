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
        // target: 'http://localhost:9999',
        target: 'http://57.180.251.211:80',
        changeOrigin: true,
        pathRewrite: {
          // '^/api': '',  // 将 /api 前缀重写为空，即去掉 /api
        },
      },
    },
  },

  transpileDependencies: [
    'vuetify'
  ]
};
//lihao测试
