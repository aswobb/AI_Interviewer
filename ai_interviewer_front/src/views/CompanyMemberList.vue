<template>
    <div>
        <v-btn icon @click="back">
            <v-icon>mdi-exit-to-app</v-icon>
        </v-btn>

        <v-data-table class="equa-width-table" disable-sort="false" :headers="headers" :items="memberList" item-key="id"
            :options.sync="tableOptions" :server-items-length="totalItems">
            <template v-slot:[`item.action`]="{ item }">
                <v-checkbox v-model="selectedItems" :value="item.id" @change="updateSelection"></v-checkbox>
            </template>
            <template v-slot:item.uploadStatus="{ item }">
                {{ getUploadStatusText(item.uploadStatus) }}
            </template>
            <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                    <v-btn class="green-button" @click="deleteButton(item.id)">
                        削除
                    </v-btn>
                    <v-btn class="green-button" @click="openDialog(item)">{{
                        item.uploadStatus === 1 ? '履歴書更新' : '履歴書アップロード' }}</v-btn>
                </div>
            </template>
        </v-data-table>
        <v-card-actions class="justify-center">
            <v-btn class="green-button" dark @click="addDataFlag = true">会社員追加</v-btn>
            <v-btn class="green-button" dark @click="openDelDialog">一括削除</v-btn>
        </v-card-actions>
        <el-dialog append-to-body title="履歴書アップロードしてください" :visible.sync="dialogVisible" width="30%">
            <span>
                <input type="file" @change="handleFileUpload" />

            </span>
            <span slot="footer" class="dialog-footer">
                <el-button class="green-button" @click="submitFile">アップロード</el-button>
                <el-button class="green-button" @click="dialogVisible = false">キャンセル</el-button>
            </span>
        </el-dialog>

        <el-dialog :visible.sync="deleteMembersFlag" width="30%">
            <p>選択したデータを削除してもよろしいですか？</p>
            <span slot="footer" class="dialog-footer">
                <el-button class="green-button" @click="deleteMembers">確認</el-button>
                <el-button class="green-button" @click="deleteMembersFlag = false">キャンセル</el-button>
            </span>
        </el-dialog>



        <el-dialog :visible.sync="deleteFlag" width="30%">
            <p>本当にこのデータを削除してもよろしいですか？</p>
            <span slot="footer" class="dialog-footer">
                <el-button class="green-button" @click="deleteMember">確認</el-button>
                <el-button class="green-button"@click="deleteFlag = false">キャンセル</el-button>
            </span>
        </el-dialog>
        <el-dialog :show-close="false" :close-on-click-modal="false" title="会社員追加" width="300px"
            :visible.sync="addDataFlag" class="dialog-style">
            <el-form :model="form" :rules="rules" ref="form">
                <el-form-item style="color: black" label="会社員名前" prop="name">
                    <el-input v-model="form.name"></el-input>
                    <el-input v-show="false"></el-input>
                </el-form-item>
                <div style="text-align: center;">
                    <el-button class="green-button" @click="submitForm('form')">提出</el-button>
                    <el-button class="green-button" @click="addDataFlag = false">キャンセル</el-button>
                </div>
            </el-form>
        </el-dialog>

    </div>
</template>
<script>
import { Message } from "element-ui";
import { memberPlus, memberDelete, fileSend2, memberListGet, companyMembersDelAPI } from '@/api'
export default {
    created() {
        this.userId = this.$route.query.id
        this.getMember(this.userId, 1, 5)
    },
    data() {
        return {
            selectedItems: [],
            deleteMembersFlag: false,
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
                { text: '複数', value: 'action', sortable: false },
                { text: '会社員名', value: 'name' },
                { text: '履歴書状態', value: 'uploadStatus' },
                { text: '操作', value: 'actions', sortable: false }
            ],
        }
    },
    methods: {
        openDelDialog() {
            console.log(this.selectedItems);
            if (this.selectedItems.length == 0) {
                Message.warning("先に削除したいデータを選んでください！")
            } else {
                this.deleteMembersFlag = true
            }
        },
        async deleteMembers() {
            const response = await companyMembersDelAPI(this.selectedItems)
            if (response.data.state == 20000) {
                Message.success("削除しました！")
                this.getMember(this.userId, this.tableOptions.page, this.tableOptions.itemsPerPage)
                this.deleteMembersFlag = false
                this.selectedItems = []
            }
        },
        updateSelection() {
            console.log(109, this.selectedItems);
        },
        submitForm(formName) {
            this.$refs[formName].validate(async (valid) => {
                if (valid) {
                    this.form.userId = this.userId
                    let data = this.form
                    const response = await memberPlus(data)
                    this.form.name = ''
                    this.addDataFlag = false
                    if (response.data.state == 20000) {
                        Message.success("追加しました！")
                        this.getMember(this.userId, this.tableOptions.page, this.tableOptions.itemsPerPage)
                    }
                }
            });
        },
        deleteButton(id) {
            this.deleteId = id
            this.deleteFlag = true
        },
        async deleteMember() {
            let obj = { memberId: this.deleteId }
            const response = await memberDelete(obj)
            this.deleteFlag = false
            if (response.data.state == 20000) {
                Message.success("削除しました！")
                this.getMember(this.userId, this.tableOptions.page, this.tableOptions.itemsPerPage)
            }
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
                const response = await fileSend2(formData)
                if (response.data.state == 20000) {
                    Message.success("履歴書を解析しました！")
                    this.selectedFile = null
                    this.dialogVisible = false
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
        async getMember(id1, pageNum1, pageSize1) {
            const response = await memberListGet(id1, pageNum1, pageSize1)
            if (response.data.state == 20000) {
                this.$store.state.companyMemberInfo = response.data.data.records
                this.memberList = response.data.data.records
                this.totalItems = response.data.data.total
                if (response.data.data.size == -1) {
                    this.totalItems = 1
                }
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
.green-button {
  background-color: rgb(0, 155, 99) !important;
  color: white !important;
}
</style>