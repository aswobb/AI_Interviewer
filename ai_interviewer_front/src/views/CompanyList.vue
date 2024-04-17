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
                課金コ-ス選択:
                <div class="a">
                    <v-radio-group v-model="userInfo.courseId" row :rules="rules.select">
                        <v-radio v-for="n in [3, 4, 5]" :key="n" :label="lable[n - 1]" :value="n"></v-radio>
                    </v-radio-group>
                </div>
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
            operation: '',
            lable: ['', '', 'ペ-シック', 'スタンダ-ド', 'プレミアム'],
            rules: {
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
                ]
            },

            userInfo: {
                username: '',
                courseId: 3,
                contractor: '',
                password: ''
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
        openDialog(flag) {
            if (flag === 1) {
                this.operation = '追加'
            } else {
                this.operation = '変更'
                console.log(108, flag);
                this.userInfo.username = flag.username
                this.userInfo.contractor = flag.contractor
                this.userInfo.courseId = flag.courseId
                this.userInfo.password = flag.password
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
                courseId: 3,
                contractor: '',
                password: ''
            }
        },
        handleChange() {
            if (this.selectedOption && this.selectedOption.length > 1) {
                this.selectedOption = [this.selectedOption.pop()];
            }
            console.log(79, this.selectedOption);
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

            }
        },
        addUser(token) {
            let url = 'api/snsUser/create'
            let config = {
                headers: {
                    'token': token
                },
            };
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