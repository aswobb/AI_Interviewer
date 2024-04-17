<template>
    <div>
        <v-data-table :headers="headers" :items="companyInfo" item-key="id" class="elevation-1"
            :options.sync="tableOptions" :server-items-length="totalItems">
            <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                    <v-btn color="primary" :disabled="!item.executionDate" class="mx-2" @click="download(item.id)">
                        ダウンロード
                    </v-btn>
                    <!-- 每一行的更改按钮 -->s
                    <v-btn color="primary" class="mx-2" @click="openChangeInfo(item)">情報の変更</v-btn>
                </div>
            </template>
        </v-data-table>

    </div>
</template>

<script>
export default {
    created() {
        this.getCompanyInfo()
    },
    data() {
        return {
            //公司集合
            companyInfo: {
            },
            dialog: false,
            //控制页数页码
            tableOptions: {
                page: 1,
                itemsPerPage: 5,
                // sortBy: ['name'],
                multiSort: false,
                mustSort: false// ...
            },
            //总页数
            totalItems: null,
            //表头属性
            headers: [
                { text: '株式会社', value: 'contractor' },
                // { text: 'ベ-シックコ-ス ', value: 'interviewerName' },
                // { text: '加入月', value: 'joinTime' },
                // { text: '当月利用数', value: 'actions' },
                // { text: '残', value: 'remainNum' }
            ],

        };
    },
    methods: {
        //公司基本信息赋值
        getCompanyInfo() {
            const token = localStorage.getItem('token');
            console.log(token);
            if (token) {
                let url = 'api/snsUser/list '
                this.axios.get(url, {
                    headers: {
                        'token': token
                    },

                }).then((response) => {
                    if (response.data.state == 20000) {
                        this.$store.commit('initCompanyInfo', response.data)
                        console.log(66, this.$store.state.companyInfo);
                        this.companyInfo = this.$store.state.companyInfo
                        console.log(68, this.companyInfo);
                    } else {
                        this.$notify.error({
                            message: '情報の取得に失敗しました',
                            type: 'error'
                        });
                    }
                });
            }
        },
        //修改页签
        changePage(pageNum1, pageSize1) {
            const token = localStorage.getItem('token');
            if (token) {
                let url = 'api/interviewerInfo/list'
                this.axios.get(url, {
                    params: { pageNum: pageNum1, pageSize: pageSize1 },
                    headers: {
                        'token': token
                    },
                }).then((response) => {
                    if (response.data.state == 20000) {
                        //面试者信息赋值
                        this.$store.commit('initInterviewerInfo', response.data)
                        this.interviewerList = this.$store.state.interviewerInfo
                        this.totalItems = this.$store.state.totalItems
                    } else {
                        this.$notify.error({
                            message: response.data.message,
                            type: 'error'
                        });
                    }
                });
            } else {
                this.$router.push('/manage-login');
                this.$message({
                    message: 'ログインが期限切れです。再度ログインしてください',
                    type: 'warn'
                });
            }
        }
    },
    //监听器
    watch: {
        'tableOptions.page': function (newPage) {
            // 当前页数变化时的处理逻辑
            console.log('当前页签:', newPage, '最大容量:', this.tableOptions.itemsPerPage);
            this.changePage(newPage, this.tableOptions.itemsPerPage)
        },
        'tableOptions.itemsPerPage': function (newRowsPerPage) {
            // 最大容量变化时的处理逻辑
            console.log('当前页签:', this.tableOptions.page, '最大容量:', newRowsPerPage,);
            this.changePage(this.tableOptions.page, newRowsPerPage)
        }
    }
};
</script>
<style>
.v-dialog {
    background: #fff;
    height: 350px;
}
</style>