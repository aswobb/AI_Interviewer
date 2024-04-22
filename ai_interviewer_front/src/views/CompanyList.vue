<template>
    <div>
        <v-btn icon @click="logout">
            <v-icon>mdi-exit-to-app</v-icon>
        </v-btn>

        <v-data-table :headers="headers" :items="companyInfo" item-key="id" class="elevation-1"
            :options.sync="tableOptions" :server-items-length="totalItems">
            <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                    <v-btn color="primary" class="mx-2" @click="openDialog(item)">
                        変更
                    </v-btn>
                </div>
            </template>
        </v-data-table>
        <v-card-actions class="justify-center">
            <v-btn color="primary" dark @click="openDialog(1)">ユーザーの追加</v-btn>
        </v-card-actions>
        <v-dialog v-model="addUserDialog" max-width="400">
            <v-card-title class="headline">ユーザーの追加</v-card-title>
            <v-form ref="form" :model="userInfo" lazy-validation>
                <v-text-field v-model="userInfo.username" label="ユーザー名" :rules="rules.username"></v-text-field>
                <v-text-field v-model="remainDialog" v-if="operation === '変更'" readonly label="現在残数"></v-text-field>
                課金コ-ス選択:
                <div class="a">
                    <v-radio-group v-model="selection" row>

                        <v-radio @click.native="a(n)" v-for="n in [3, 4, 5, 6]" :key="n" :label="lable[n - 1]"
                            :value="n"></v-radio>
                    </v-radio-group>
                </div>
                <v-text-field v-model="userInfo.userBillingHistoryVO.courseCustomNum"
                    v-if="userInfo.userBillingHistoryVO.courseId === 6" label="カスタム数量を入力してください"
                    :rules="computedRules"></v-text-field>
                <v-text-field v-model="userInfo.remainNum" :rules="rules.remainNum"
                    v-if="operation === '変更' && (this.userInfo.userBillingHistoryVO.courseId === null)"
                    label="残数を入力してください"></v-text-field>
                <v-text-field v-model="userInfo.contractor" label="会社名" :rules="rules.companyName"></v-text-field>
                <v-text-field v-model="userInfo.password" label="パスワード" :rules="rules.password"></v-text-field>

            </v-form>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="green darken-1" @click="sumbit()">{{ operation }}</v-btn>
                <v-btn color="green darken-1" @click="close">キャンセル</v-btn>
            </v-card-actions>
        </v-dialog>
    </div>
</template>

<script>
export default {
    created() {
        this.getCompanyInfo(1, 5)
        this.companyInfo = this.$store.state.companyInfo
        this.totalItems = this.$store.state.companyTotalItems
    },
    data() {
        return {
            selection: 0,
            remainDialog: 0,
            //对话框记录剩余次数
            operation: '',
            lable: ['', '', 'ペ-シック', 'スタンダ-ド', 'プレミアム', 'その他'],
            rules: {
                remainNum: [
                    v => /^\d*$/.test(v) || '数値のみ入力可能'
                ],
                username: [
                    v => !!v || 'ユーザー名を入力してください',
                    v => (v && v.length <= 15) || '文字数は2から15文字まで',
                    v => (v && v.length >= 2) || '文字数は2から15文字まで',
                ],
                password: [
                    v => !!v || 'パスワードを入力してください',
                    v => (v && v.length <= 15) || '文字数は4から15文字まで',
                    v => (v && v.length >= 4) || '文字数は4から15文字まで',
                ],
                companyName: [
                    v => !!v || '会社名を入力してください',
                ],
                customRule: [
                    v => !!v || '残りの回数を入力してください',
                    v => /^\d*$/.test(v) || '数値のみ入力可能'
                ]
            },

            userInfo: {
                id: null,
                username: '',
                contractor: '',
                password: '',
                userBillingHistoryVO: {
                    courseId: null,
                    courseCustomNum: 0
                },
                remainNum: 0
            },
            //增加用户会话开关
            addUserDialog: false,
            //修改用户会话开关
            updateUserDialog: false,
            //控制页数页码
            tableOptions: {
                page: 1,
                itemsPerPage: 5,
                // sortBy: ['name'],
                multiSort: false,
                mustSort: false// ...
            },
            //总页数
            totalItems: 0,
            //公司集合
            companyInfo: {
            },
            headers: [
                { text: '株式会社', value: 'contractor' },
                { text: 'ユーザー名', value: 'username' },
                { text: 'ベ-シックコ-ス ', value: 'courseName' },
                { text: '加入時期', value: 'joinTime' },
                { text: '使用回数', value: 'usageCount' },
                { text: '残数', value: 'remainNum' },
                { text: '操作', value: 'actions' },
            ]
        };
    },
    methods: {
        a(n) {
            if (n === this.userInfo.userBillingHistoryVO.courseId) {
                this.selection = 0
                this.userInfo.userBillingHistoryVO.courseId = null
                this.userInfo.remainNum = null
            } else {
                this.userInfo.userBillingHistoryVO.courseId = n
                this.selection = n
                this.userInfo.remainNum = null
            }
        },
        openDialog(flag) {
            if (flag === 1) {
                this.operation = '追加'
            } else {
                this.operation = '変更'
                console.log(108, flag);
                this.userInfo.id = flag.id
                this.userInfo.username = flag.username
                this.userInfo.contractor = flag.contractor
                this.userInfo.userBillingHistoryVO.courseId = null
                this.userInfo.password = flag.password
                this.remainDialog = flag.remainNum
            }
            this.addUserDialog = true

        },
        logout() {
            this.$router.push('/manage-login')
            this.$store.commit('cleanCache')
        },
        close() {
            this.addUserDialog = false
            this.userInfo = {
                username: '',
                contractor: '',
                password: '',
                userBillingHistoryVO: {
                    courseId: 3,
                    courseCustomNum: 0
                }
            }
        },
        //公司基本信息赋值
        getCompanyInfo(pageNum1, pageSize1) {
            const token = localStorage.getItem('token');
            console.log(token);
            if (token) {
                let url = 'api/snsUser/list'
                this.axios.get(url, {
                    params: { pageNum: pageNum1, pageSize: pageSize1 },
                    headers: {
                        'token': token
                    },

                }).then((response) => {
                    if (response.data.state == 20000) {
                        console.log(response);
                        this.$store.commit('initCompanyInfo', response.data)
                        this.companyInfo = this.$store.state.companyInfo
                        this.totalItems = this.$store.state.companyTotalItems
                    } else {
                        this.$notify.error({
                            message: '情報の取得に失敗しました',
                            type: 'error'
                        });
                    }
                });
            }
        },
        sumbit() {
            const token = localStorage.getItem('token');
            if (token) {
                if (this.$refs.form.validate()) {

                    if (this.operation === '追加') {
                        this.addUser(token)
                    } else {
                        this.updateUser(token)
                    }
                }

            } else {
                this.$router.push('/manage-login');
                this.$message({
                    message: 'ログインが期限切れです。再度ログインしてください',
                    type: 'warn'
                });

            }
        },
        addUser(token) {
            let url = 'api/snsUser/create'
            let config = {
                headers: {
                    'token': token
                },
            };
            console.log("add", this.userInfo);
            this.axios.post(url, this.userInfo, config).then((response) => {
                if (response.data.state == 20000) {
                    this.$message({
                        message: 'ユーザーの追加が成功しました.',
                        type: 'success'
                    });
                    //回显
                    this.getCompanyInfo(this.tableOptions.page, this.tableOptions.itemsPerPage)
                    //关闭对话框
                    this.close()
                } else {
                    this.$message({
                        message: response.data.message,
                        type: 'error'
                    });
                }

            });
        },
        updateUser(token) {
            let url = 'api/snsUser/update'
            let config = {
                headers: {
                    'token': token
                },
            };
            this.axios.post(url, this.userInfo, config).then((response) => {
                if (response.data.state == 20000) {
                    this.$message({
                        message: 'ユーザーの変更が成功しました.',
                        type: 'success'
                    });
                    //回显
                    this.getCompanyInfo(this.tableOptions.page, this.tableOptions.itemsPerPage)
                    //关闭对话框
                    this.close()
                } else {
                    this.$message({
                        message: response.data.message,
                        type: 'error'
                    });
                }

            });
        }

    },
    //监听器
    watch: {
        'tableOptions.page': function (newPage) {
            // 当前页数变化时的处理逻辑
            console.log('当前页签:', newPage, '最大容量:', this.tableOptions.itemsPerPage);
            this.getCompanyInfo(newPage, this.tableOptions.itemsPerPage)
        },
        'tableOptions.itemsPerPage': function (newRowsPerPage) {
            // 最大容量变化时的处理逻辑
            console.log('当前页签:', this.tableOptions.page, '最大容量:', newRowsPerPage,);
            this.getCompanyInfo(this.tableOptions.page, newRowsPerPage)
        },
        'userInfo.userBillingHistoryVO.courseId': function (newCourseId) {
            this.userInfo.userBillingHistoryVO.courseCustomNum = 0
        },

    },
    computed: {
        computedRules() {
            // 当a等于1时，返回自定义规则，否则返回空数组，即不进行验证
            return this.userInfo.userBillingHistoryVO.courseId === 6 ? this.rules.customRule : [];
        }

    }
}
</script>
<style>
.v-dialog {
    background: #fff;
    height: 350px;
}

.mt-2 {
    margin-top: 1px;
    /* 上间距 */
}

.a {
    padding-left: 25px;
}
</style>
<!-- 刷新同步 -->