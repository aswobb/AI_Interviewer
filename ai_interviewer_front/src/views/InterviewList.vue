<template>
    <div>
        <v-btn icon @click="back">
            <v-icon>mdi-exit-to-app</v-icon>
        </v-btn>
        <v-container>
            <v-row justify="center">
                <v-col cols="12" md="auto">
                    <div class="text-center">会社:{{ companyInfo.contractor }}</div>
                </v-col>
                <v-col cols="12" md="auto">
                    <div class="text-center">利用实績:{{ companyInfo.usageCount }}回</div>
                </v-col>
                <v-col cols="12" md="auto">
                    <div class="text-center">残面接:{{ companyInfo.remainNum }}回</div>
                </v-col>
            </v-row>
        </v-container>
        <v-data-table disable-sort="false" :headers="headers" :items="interviewerList" item-key="id" class="elevation-1"
            :options.sync="tableOptions" :server-items-length="totalItems">
            <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                    <v-btn color="primary" :disabled="!item.executionDate" class="mx-2" @click="download(item.id)">
                        ダウンロード
                    </v-btn>
                    <!-- 每一行的更改按钮 -->
                    <v-btn color="primary" class="mx-2" @click="openChangeInfo(item)">情報の変更</v-btn>
                </div>
            </template>
        </v-data-table>
        <v-card-actions class="justify-center">
            <v-btn color="primary" dark @click="addData">面接者データを20件追加</v-btn>
        </v-card-actions>
        <!-- 更改信息弹出框 -->
        <v-dialog v-model="dialog" max-width="400">
            <v-card-title class="headline">情報の変更</v-card-title>
            <v-form ref="form" :model="changeInfo" lazy-validation>
                <v-text-field v-model="changeInfo.interviewerId" label="面接id" readonly></v-text-field>
                <v-text-field v-model="changeInfo.interviewerName" :rules="name" label="面接者" required></v-text-field>
            </v-form>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="green darken-1" @click="sumbit">変更</v-btn>
                <v-btn color="green darken-1" @click="dialog = false">キャンセル</v-btn>
            </v-card-actions>
        </v-dialog>
    </div>
</template>

<script>
export default {
    created() {
        this.changePage(1, 5)
        this.interviewerList = this.$store.state.interviewerInfo
        this.totalItems = this.$store.state.totalItems
        this.getCompanyInfo()
        console.log(58, this.interviewerList);
    },
    data() {
        return {
            companyInfo: {
                contractor: '',
                remainNum: 0,
                usageCount: 0
            },
            //旧名称储存用于比较新名称校验规则
            oldName: '',
            //更改姓名校验规则
            name: [
                v => !!v || '面接者名を入力してください',
                v => (v && v !== this.oldName) || '新しい面接者名を入力してください'
            ],
            //更改弹出框表单信息
            changeInfo: {
                id: null,
                interviewerName: '',
                interviewerId: ''

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
                { text: '面接id', value: 'interviewerId' },
                { text: '面接者', value: 'interviewerName' },
                { text: '実施日', value: 'executionDate' },
                { text: '操作', value: 'actions', sortable: false }
            ],
            //面试者信息
            interviewerList: {},
        };
    },
    methods: {
        back() {
            this.$router.push('/manage-info')
        },
        //公司基本信息赋值
        getCompanyInfo() {
            const token = localStorage.getItem('token');
            console.log(token);
            if (token) {
                let url = 'api/snsUser/getCurrentUser'
                this.axios.get(url, {
                    headers: {
                        'token': token
                    },

                }).then((response) => {
                    if (response.data.state == 20000) {
                        console.log(response);
                        this.companyInfo.contractor = response.data.data.contractor
                        this.companyInfo.remainNum = response.data.data.remainNum
                        this.companyInfo.usageCount = response.data.data.usageCount

                    } else if (response.data.state == 40400) {
                        this.$router.push("/manage-login")
                        this.$notify.warning({
                            message: 'ログインが期限切れです,再度ログインしてください',
                            type: 'warn'
                        });
                    } else {
                        this.$notify.error({
                            message: '面接者情報の取得に失敗しました',
                            type: 'error'
                        });
                    }
                });
            } else {
                this.$router.push('/manage-login');
                this.$message({
                    message: 'ログインが期限切れです,再度ログインしてください',
                    type: 'warn'
                });
            }
        },

        //增加条数
        addData() {
            const token = localStorage.getItem('token');
            console.log(token);
            if (token) {

                let data = {}
                let url = 'api/interviewerInfo/batchCreate'
                this.axios.post(url, data, {
                    headers: {
                        'token': token
                    },

                }).then((response) => {
                    if (response.data.state == 20000) {
                        this.changePage(this.tableOptions.page, this.tableOptions.itemsPerPage)
                        this.getCompanyInfo()
                        this.$message({
                            message: '面接者データ20件追加成功しました。',
                            type: 'success'
                        })
                    } else if (response.data.state == 40400) {
                        this.$router.push("/manage-login")
                        this.$notify.warning({
                            message: 'ログインが期限切れです,再度ログインしてください',
                            type: 'warn'
                        });
                    } else if (response.data.state == 70001) {
                        this.$notify.error({
                            message: '残高が不足です。',
                            type: 'error'
                        });
                    } else {

                        this.$notify.error({
                            message: '面接者データ追加に失敗しました。',
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
        },
        //打开修改面板
        openChangeInfo(item) {
            this.dialog = true
            console.log(item);
            this.oldName = item.interviewerName
            this.changeInfo.id = item.id
            this.changeInfo.interviewerName = item.interviewerName
            this.changeInfo.interviewerId = item.interviewerId
        },
        //下载结果信息
        download(Id) {
            console.log(124, this.interviewerList);
            const token = localStorage.getItem('token');
            console.log(token);
            if (token) {
                let url = 'api/interviewerInfo/downLoadCsv/' + Id
                this.axios.get(url, {
                    headers: {
                        'token': token
                    },
                    responseType: 'blob'
                }).then((response) => {
                    if (response.data.state == 40400) {
                        this.$router.push("/manage-login")
                        this.$notify.warning({
                            message: 'ログインが期限切れです,再度ログインしてください',
                            type: 'warn'
                        });
                    } else {
                        try {
                            const blob = new Blob([response.data], { type: 'application/octet-stream' }); // ダウンロードリンクを作成 
                            const url = window.URL.createObjectURL(blob);
                            const link = document.createElement('a');
                            link.href = url;
                            link.setAttribute('download', 'output.csv'); // ダウンロードファイル名を設定 // リンクをクリックしてファイルをダウンロード 
                            document.body.appendChild(link);
                            link.click(); // リンクを削除 
                            document.body.removeChild(link);
                        } catch (error) {
                            this.$message({
                                message: error.message,
                                type: 'error'
                            });
                        }
                    }
                });
            } else {
                this.$router.push('/manage-login');
                this.$message({
                    message: 'ログインが期限切れです。再度ログインしてください',
                    type: 'warn'
                });
            }
        },
        //修改面试者信息
        sumbit() {
            if (this.$refs.form.validate()) {
                const token = localStorage.getItem('token');
                console.log(token);
                if (token) {
                    let data = {
                        id: this.changeInfo.id,
                        interviewerName: this.changeInfo.interviewerName
                    }
                    let url = 'api/interviewerInfo/updateInterviewerInfo'
                    this.axios.post(url, data, {
                        headers: {
                            'token': token
                        },

                    }).then((response) => {
                        if (response.data.state == 20000) {
                            this.$message({
                                message: '変更に成功しました',
                                type: 'success'
                            });
                            //数据回显
                            const itemIndex = this.interviewerList.findIndex(item => item.id === this.changeInfo.id);
                            if (itemIndex !== -1) {
                                this.interviewerList.splice(itemIndex, 1, {
                                    ...this.interviewerList[itemIndex],
                                    interviewerName: this.changeInfo.interviewerName,
                                });
                            }
                            this.dialog = false
                        } else if (response.data.state == 40400) {
                            this.$router.push("/manage-login")
                            this.$notify.warning({
                                message: 'ログインが期限切れです,再度ログインしてください',
                                type: 'warn'
                            });
                        } else {
                            this.$notify.error({
                                message: '面接者情報の取得に失敗しました',
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
                        console.log(response);
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
    },
};
</script>
<style>
.v-dialog {
    background: #fff;
    height: 350px;
}
</style>
<!-- 刷新同步 -->