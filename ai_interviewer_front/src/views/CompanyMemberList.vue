<template>
    <div>
        <v-btn icon @click="back">
            <v-icon>mdi-exit-to-app</v-icon>
        </v-btn>

        <v-data-table class="equa-width-table" disable-sort="false" :headers="headers" :items="memberList" item-key="id"
            :options.sync="tableOptions" :server-items-length="totalItems">
            <template v-slot:item.uploadStatus="{ item }">
                {{ getUploadStatusText(item.uploadStatus) }}
            </template>
            <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                    <v-btn color="primary" class="mx-2" @click="deleteButton(item.id)">
                        削除
                    </v-btn>
                    <v-btn color="primary" class="mx-2" @click="openDialog(item)">{{
                        item.uploadStatus === 1 ? '履歴書更新' : '履歴書アップロード' }}</v-btn>
                </div>
            </template>
        </v-data-table>
        <v-card-actions class="justify-center">
            <v-btn color="primary" dark @click="addDataFlag = true">会社員追加</v-btn>
        </v-card-actions>
        <el-dialog append-to-body title="履歴書アップロードしてください" :visible.sync="dialogVisible" width="30%">
            <span>
                <input type="file" @change="handleFileUpload" />

            </span>
            <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitFile">アップロード</el-button>
                <el-button @click="dialogVisible = false">キャンセル</el-button>
            </span>
        </el-dialog>

        <el-dialog :visible.sync="deleteFlag" width="30%">
            <p>本当にこのデータを削除してもよろしいですか？</p>
            <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click="deleteMember">確認</el-button>
                <el-button @click="deleteFlag = false">キャンセル</el-button>
            </span>
        </el-dialog>
        <el-dialog :show-close="false" :close-on-click-modal="false" title="会社員追加" width="300px"
            :visible.sync="addDataFlag" class="dialog-style">
            <el-form :model="form" :rules="rules" ref="form">
                <el-form-item style="color: black" label="会社員名前" prop="name">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <div style="text-align: center;">
                    <el-button type="primary" @click="submitForm('form')">提出</el-button>
                    <el-button @click="addDataFlag = false">キャンセル</el-button>
                </div>
            </el-form>
        </el-dialog>

    </div>
</template>
<script>
import { Message } from "element-ui";
export default {
    created() {
        this.userId = this.$route.query.id
        this.getMember(this.userId, 1, 5)
    },
    data() {
        return {
            addDataFlag: false,
            form: {
                id: null,
                name: '',
                uploadStatus: 0,
                resume: "",
                userId: null
            },
            rules: {
                name: [
                    { required: true, message: '会社員名前入力してください', trigger: 'blur' },
                ],
            },
            deleteFlag: false,
            deleteId: null,
            selectedFile: null,
            memberId: 0,
            dialogVisible: false,
            totalItems: 0,
            tableOptions: {
                page: 1,
                itemsPerPage: 5,
                // sortBy: ['name'],
                multiSort: false,
                mustSort: false// ...
            },
            memberList: [],
            userId: 0,
            headers: [
                { text: '会社員名', value: 'name' },
                { text: '履歴書状態', value: 'uploadStatus' },
                { text: '操作', value: 'actions', sortable: false }
            ],
        }
    },
    methods: {
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    const token = localStorage.getItem('token')
                    console.log(104, token);
                    if (token) {
                        let url = 'api/companyMember/insert'
                        this.form.userId = this.userId
                        let data = this.form
                        this.axios.post(url, data, {
                            headers: {
                                'token': token
                            },
                        }).then((response) => {
                            this.form.name = ''
                            this.addDataFlag = false
                            console.log(111, response);
                            if (response.data == 1) {
                                Message.success("追加しました！")
                                this.getMember(this.userId, this.tableOptions.page, this.tableOptions.itemsPerPage)
                            }
                            else if (response.data.state == 40400) {
                                this.$router.push("/manage-login")
                                this.$notify.warning({
                                    message: 'ログインが期限切れです,再度ログインしてください',
                                    type: 'warn'
                                });
                            }
                        })
                    }
                }
            });
        },
        deleteButton(id) {
            this.deleteId = id
            this.deleteFlag = true
        },
        deleteMember() {
            const token = localStorage.getItem('token')
            let url = 'api/companyMember/delete'
            this.axios.delete(url, {
                params: { memberId: this.deleteId },
                headers: {
                    'token': token
                },
            }).then((response) => {
                this.deleteFlag = false
                if (response.data == 1) {
                    Message.success("削除しました！")
                    this.getMember(this.userId, this.tableOptions.page, this.tableOptions.itemsPerPage)
                } else if (response.data.state == 40400) {
                    this.$router.push("/manage-login")
                    this.$notify.warning({
                        message: 'ログインが期限切れです,再度ログインしてください',
                        type: 'warn'
                    });
                }

            })
        },
        back() {
            this.$router.push('/manage-info')
        },
        async submitFile() {
            if (!this.selectedFile) {
                Message.warning("先に、ご履歴書選択してください！")
                return
            }
            this.isInputDisabled = !this.isInputDisabled
            const formData = new FormData();
            formData.append('file', this.selectedFile);
            formData.append('id', this.memberId)
            this.determineFileType(this.selectedFile)
            try {
                const token = localStorage.getItem('token')
                console.log(77);
                const response = await this.axios.post("/api/companyMember/receiveFile", formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'token': token
                    }
                });
                if (response.data.state == 20000) {
                    Message.success("履歴書を解析しました！")
                    this.selectedFile = null
                    this.dialogVisible = false
                } else if (response.data.state == 40400) {
                    this.$router.push("/manage-login")
                    this.$notify.warning({
                        message: 'ログインが期限切れです,再度ログインしてください',
                        type: 'warn'
                    });
                } else {

                }
                this.getMember(this.userId, this.tableOptions.page, this.tableOptions.itemsPerPage)
                this.dialogVisible = false
            } catch (error) {
                Message.error('履歴書アップロード失敗しました')
            }

        },

        determineFileType(file) {
            const fileName = file.name.toLowerCase();
            const mimeType = file.type;

            const allowedTypes = [
                'application/pdf',
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                'application/vnd.ms-excel',
                'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                'application/msword'
            ];

            if (!allowedTypes.includes(mimeType)) {
                Message.warning("WORD、EXCELまたはPDF形式の履歴書をアップロードしてください！")
                throw new Error('条件不满足，请求终止');
            }
        },


        handleFileUpload(event) {
            this.selectedFile = event.target.files[0];
        },
        openDialog(item) {
            this.memberId = item.id
            this.dialogVisible = true
        },
        getUploadStatusText(status) {
            return status === 1 ? '済み' : 'ー';
        },
        getMember(id1, pageNum1, pageSize1) {
            const token = localStorage.getItem('token');
            console.log(token);
            if (token) {
                let url = 'api/companyMember/list'
                this.axios.get(url, {
                    params: { userId: id1, pageNum: pageNum1, pageSize: pageSize1 },
                    headers: {
                        'token': token
                    },

                }).then((response) => {
                    console.log(52, response);
                    if (response.data.state == 20000) {
                        this.$store.state.companyMemberInfo = response.data.data.records
                        this.memberList = response.data.data.records
                        this.totalItems = response.data.data.total
                        if (response.data.data.size == -1) {
                            this.totalItems = 1
                        }
                    } else if (response.data.state == 40400) {
                        this.$router.push("/manage-login")
                        this.$notify.warning({
                            message: 'ログインが期限切れです,再度ログインしてください',
                            type: 'warn'
                        });
                    }
                })
            }
        }
    },
    //监听器
    watch: {
        'tableOptions.page': function (newPage) {
            // 当前页数变化时的处理逻辑
            console.log('当前页签:', newPage, '最大容量:', this.tableOptions.itemsPerPage);
            this.getMember(this.userId, newPage, this.tableOptions.itemsPerPage)
        },
        'tableOptions.itemsPerPage': function (newRowsPerPage) {
            // 最大容量变化时的处理逻辑
            console.log('当前页签:', this.tableOptions.page, '最大容量:', newRowsPerPage,);
            this.getMember(this.userId, this.tableOptions.page, newRowsPerPage)
        }
    },
}
</script>
<style>
.equa-width-table .v-data-table__wrapper table th,
.equa-width-table .v-data-table__wrapper table td {
    width: 33.33%;
}

.dialog-style {
    height: 600px;
}
</style>