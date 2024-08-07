<template>
    <div>
        <v-btn icon @click="logout">
            <v-icon>mdi-exit-to-app</v-icon>
        </v-btn>
        <v-data-table :disable-sort="false" :headers="headers" :items="companyInfo" item-key="id" class="elevation-1"
            :options.sync="tableOptions" :server-items-length="totalItems">
            <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                    <v-btn class="green-button" @click="openDialog(item)">
                        変更
                    </v-btn>
                    <v-btn class="green-button" @click="showDeleteConfirmation(item.id)">
                        削除
                    </v-btn>
                </div>
            </template>
        </v-data-table>
        <v-card-actions class="justify-center">
            <v-btn class="green-button" dark @click="openDialog(1)">ユーザーの追加</v-btn>
        </v-card-actions>
        <v-dialog v-model="addUserDialog" max-width="400">
            <v-card-title class="headline">ユーザーの{{ operation }}</v-card-title>
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
                <v-btn class="green-button" @click="sumbit()">{{ operation }}</v-btn>
                <v-btn class="green-button" @click="close">キャンセル</v-btn>
            </v-card-actions>
        </v-dialog>


        <el-dialog :show-close="false" :close-on-click-modal="false" title="ユーザー削除" width="300px" :visible.sync="dialog"
            class="dialog-style">
            <p>本当にこのデータを削除してもよろしいですか？</p>
            <div style="text-align: center;">
                <el-button class="green-button" @click="deleteData">はい</el-button>
                <el-button class="green-button" @click="closeDialog">いいえ</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { companyDelAPI, getCompanyAPI, managerPlus, managerUpdate } from '@/api'
export default {
    created() {
        this.getCompanyInfo(1, 5)
        this.companyInfo = this.$store.state.companyInfo
        this.totalItems = this.$store.state.companyTotalItems
    },
    data() {
        return {
            deleteId: null,
            dialog: false,
            selection: 0,
            remainDialog: 0,
            //对话框记录剩余次数
            operation: '',
            lable: ['', '', 'ペ-シック', 'スタンダ-ド', 'プレミアム', 'その他'],
            rules: {
                remainNum: [
                    v => {
                        if (v === null || v === undefined) {
                            return true; // 输入为空或者 null，则验证通过

                        }
                        // 输入不为空，继续验证是否为数字
                        return /^\d*$/.test(v) || '数値のみ入力可能';
                    }
                ],
                username: [
                    v => !!v || 'ユーザー名を入力してください',
                    v => (v && v.length <= 10) || 'ユーザ名10桁以下入力してください',
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
                remainNum: null
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
        showDeleteConfirmation(id) {
            this.dialog = true;
            this.deleteId = id

        },
        closeDialog() {
            this.dialog = false;
        },

        async deleteData() {
            const response = await companyDelAPI(this.deleteId)

            if (response.data == 1) {
                this.dialog = false
                this.getCompanyInfo(this.tableOptions.page, this.tableOptions.itemsPerPage)
                this.$message({
                    message: '削除に成功しました！',
                    type: 'success'
                })
            }
        },
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
                this.selection = 0
                this.userInfo.username = ''
                this.userInfo.contractor = ''
                this.userInfo.password = ''
                this.userInfo.userBillingHistoryVO.courseId = null
            } else {
                this.operation = '変更'
                console.log(108, flag);
                this.userInfo.id = flag.id
                this.userInfo.username = flag.username
                this.userInfo.contractor = flag.contractor
                this.userInfo.userBillingHistoryVO.courseId = null
                this.userInfo.password = flag.password
                this.remainDialog = flag.remainNum
                this.selection = 0
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
        async getCompanyInfo(pageNum1, pageSize1) {
            const response = await getCompanyAPI(pageNum1, pageSize1)

            if (response.data.state == 20000) {
                console.log(response);
                this.$store.commit('initCompanyInfo', response.data)
                this.companyInfo = this.$store.state.companyInfo
                this.totalItems = this.$store.state.companyTotalItems
            }
        },
        sumbit() {
            if (this.$refs.form.validate()) {

                if (this.operation === '追加') {
                    this.addUser()
                } else {
                    this.updateUser()
                }
            }

        },


        async addUser() {
            const response = await managerPlus(this.userInfo);
            if (response.data.state == 20000) {
                this.$message({
                    message: 'ユーザーの追加が成功しました.',
                    type: 'success'
                });
                //回显
                this.getCompanyInfo(this.tableOptions.page, this.tableOptions.itemsPerPage)
                //关闭对话框
                this.close()
            }
        },


        async updateUser() {

            const response = await managerUpdate(this.userInfo);
            if (response.data.state == 20000) {
                this.$message({
                    message: 'ユーザーの変更が成功しました.',
                    type: 'success'
                });
                // 回显
                this.getCompanyInfo(this.tableOptions.page, this.tableOptions.itemsPerPage)
                //关闭对话框
                this.close()
            }
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

    },
    beforeDestroy() {
        // 清理逻辑，例如重置Vuex状态
        this.$store.commit('cleanCache');
    },
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

.modal {
    display: block;
    position: fixed;
    z-index: 1;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
    background-color: #fefefe;
    margin: 15% auto;
    padding: 20px;
    border: 1px solid #888;
    width: 50%;
    /* 调整宽度 */
    max-width: 300px;
    /* 最大宽度 */
}

.confirm-button,
.cancel-button {
    background-color: #f44336;
    color: white;
    padding: 10px 20px;
    margin: 5px;
    border: none;
    cursor: pointer;
    border-radius: 5px;
}

.confirm-button:hover,
.cancel-button:hover {
    background-color: #d32f2f;
}

.container {
    display: flex;
    justify-content: center;
    /* 水平居中 */
    align-items: center;
    /* 垂直居中 */
    height: 50px;
    /* 容器高度 */
}

.green-button {
    background-color: rgb(0, 155, 99) !important;
    color: white !important;
    margin: 0 10px;
    /* 按钮之间的间距 */
}
</style>
<!-- 刷新同步 -->