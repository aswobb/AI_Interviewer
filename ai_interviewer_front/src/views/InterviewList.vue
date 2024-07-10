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

        <v-data-table :disable-sort="false" :headers="headers" :items="interviewerList" item-key="id"
            class="elevation-1" :options.sync="tableOptions" :server-items-length="totalItems">
            <template v-slot:item.checkbox="{ item }">
                <v-checkbox v-model="item.checkbox" :disabled="!Boolean(item.executionDate)"
                    @change="toggleSelection(item)"></v-checkbox>
            </template>
            <template v-slot:item.uploadStatus="{ item }">
                {{ getUploadStatusText(item.uploadStatus) }}
            </template>
            <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                    <v-btn color="primary" :disabled="!Boolean(item.executionDate)" class="mx-2"
                        @click="download(item)">
                        ダウンロード
                    </v-btn>
                    <!-- 每一行的更改按钮 -->
                    <v-btn color="primary" :disabled="Boolean(item.executionDate)" class="mx-2"
                        @click="openChangeInfo(item)">情報の変更</v-btn>

                </div>
            </template>
        </v-data-table>
        <v-card-actions class="justify-center">
            <v-btn color="primary" dark @click="downLoadCsvs">一括ダウンロード</v-btn>
            <v-btn color="primary" dark @click="addData">面接者データを20件追加</v-btn>
        </v-card-actions>
        <!-- 更改信息弹出框 -->
        <v-dialog v-model="dialog" max-width="400">
            <v-card-title class="headline">情報の変更</v-card-title>
            <v-form ref="form" :model="changeInfo" lazy-validation>
                <v-text-field v-model="changeInfo.interviewerId" label="面接id" readonly></v-text-field>
                <v-combobox v-model="companyMemberInfo" :items="memberList" label="Search" item-text="name"
                    item-value="id" clearable :search-input.sync="search"></v-combobox>
            </v-form>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="green darken-1" @click="sumbit">変更</v-btn>
                <v-btn color="green darken-1" @click="cancel">キャンセル</v-btn>
            </v-card-actions>
        </v-dialog>
    </div>
</template>

<script>
import { Message } from 'element-ui';
import { memberGet, getCurrentUserAPI, interviewInfoDownload, interviewListAdd, interviewInfoUpdate, getInterviewMessageAPI, downLoadCsvsAPI } from '@/api'
import JSZip from 'jszip';

export default {
    created() {
        this.userId = this.$route.query.id
        this.getMember(this.userId)
        this.changePage(1, 5)
        this.interviewerList = this.$store.state.interviewerInfo
        this.totalItems = this.$store.state.totalItems
        this.getCompanyInfo()
        console.log(58, this.interviewerList);
    },
    data() {
        return {
            selectedItems: [],
            flag: false,
            search: '',
            companyMemberInfo: null,
            memberList: [],
            userId: 0,
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
                { text: 'チェック', value: 'checkbox' },
                { text: '面接id', value: 'interviewerId' },
                { text: '面接者', value: 'interviewerName' },
                { text: '履歴書状態', value: 'uploadStatus' },
                { text: '実施日', value: 'executionDate' },
                { text: '操作', value: 'actions', sortable: false }
            ],
            //面试者信息
            interviewerList: {},
        };
    },
    methods: {
        cancel() {
            this.dialog = false
            this.companyMemberInfo = null
        },
        getUploadStatusText(status) {
            return status === 1 ? '済み' : 'ー';
        },
        async getMember(id) {
            let obj = {
                userId: id,
            }
            const response = await memberGet(obj)
            if (response.data.state == 20000) {
                this.$store.state.companyMemberInfo = response.data.data
                this.memberList = response.data.data
                console.log(129, this.memberList);
            }
        },
        back() {
            this.$router.push('/manage-info')
        },
        //公司基本信息赋值
        async getCompanyInfo() {
            const response = await getCurrentUserAPI();
            if (response.data.state == 20000) {
                console.log(response);
                this.companyInfo.contractor = response.data.data.contractor
                this.companyInfo.remainNum = response.data.data.remainNum
                this.companyInfo.usageCount = response.data.data.usageCount
            }
        },

        //增加条数
        async addData() {
            let data = {}
            const response = await interviewListAdd(data);
            if (response.data.state == 20000) {
                this.changePage(this.tableOptions.page, this.tableOptions.itemsPerPage)
                this.getCompanyInfo()
                this.$message({
                    message: '面接者データ20件追加成功しました。',
                    type: 'success'
                })
            }
        },
        //打开修改面板
        async openChangeInfo(item) {
            this.dialog = true
            console.log(item);
            this.oldName = item.interviewerName
            this.changeInfo.id = item.id
            this.changeInfo.interviewerName = item.interviewerName
            this.changeInfo.interviewerId = item.interviewerId
        },
        // //下载结果信息
        async download(item) {
            console.log(180, item);
            let id = item.id
            const response = await interviewInfoDownload(id);
            console.log(207, response);
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
                    link.setAttribute('download', item.interviewerName + item.executionDate + '.csv'); // ダウンロードファイル名を設定 // リンクをクリックしてファイルをダウンロード 
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
        },

        // CSVファイル　複数ダウンロード
        async downLoadCsvs() {
            if (this.selectedItems.length > 0) {
                const userIds = this.selectedItems
                console.log(userIds);
                const response = await downLoadCsvsAPI(userIds);

                console.log(response)
                try {
                    const zip = new JSZip();
                    const content = await zip.loadAsync(response.data);

                    // 提取 ZIP 文件中的每个文件并自动下载
                    content.forEach(async (relativePath, file) => {
                        const blob = await file.async('blob');
                        const url = URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.href = url;
                        a.download = relativePath;
                        document.body.appendChild(a);
                        a.click();
                        document.body.removeChild(a);
                        URL.revokeObjectURL(url); // 释放 URL 对象
                    });
                    this.interviewerList.forEach(item => {
                        item.checkbox = false; // 假设表格数据中有一个 isSelected 属性用于管理选中状态
                    });
                    this.selectedItems
                    Message.success("アップロード成功しました！")
                } catch (error) {
                    this.interviewerList.forEach(item => {
                        item.checkbox = false; // 假设表格数据中有一个 isSelected 属性用于管理选中状态
                    });
                    this.selectedItems = []
                    console.error('Failed to download ZIP file:', error);
                    this.$message({
                        message: 'Failed to download ZIP file.',
                        type: 'error'
                    });
                }
            } else {
                Message.warning("少なくとも1つのファイルを選択してください！");
            }
        },

        //
        async downloadByIds() {
            const response = await interviewInfoDownload(Id);
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
        },

        //修改面试者信息
        async sumbit() {
            this.memberList.some(item => {
                if (item.name === this.search) {
                    this.flag = true
                    return true; // 停止遍历
                }
                return false;
            });
            console.log(this.flag);
            if (this.flag) {
                this.flag = false
                if (this.$refs.form.validate()) {
                    let data = {
                        id: this.changeInfo.id,
                        interviewerName: this.companyMemberInfo.name,
                        companyMemberId: this.companyMemberInfo.id,
                        uploadStatus: this.companyMemberInfo.uploadStatus
                    }
                    this.companyMemberInfo = null
                    const response = await interviewInfoUpdate(data)
                    if (response.data.state == 20000) {
                        this.$message({
                            message: '変更に成功しました',
                            type: 'success'
                        });
                        //数据回显
                        this.changePage(this.tableOptions.page, this.tableOptions.itemsPerPage)
                        this.dialog = false
                    }
                }
            } else {
                Message.error("会社ではこの名前がありません")
            }
        },
        //修改页签
        async changePage(pageNum1, pageSize1) {
            const response = await getInterviewMessageAPI(pageNum1, pageSize1);
            if (response.data.state == 20000) {
                console.log(response);
                //面试者信息赋值
                this.$store.commit('initInterviewerInfo', response.data)
                this.interviewerList = this.$store.state.interviewerInfo
                this.totalItems = this.$store.state.totalItems
            }
        },

        toggleSelection(item) {
            // 切换选中状态时更新 selectedItems 数组
            const index = this.selectedItems.indexOf(item.id);
            if (index !== -1) {
                this.selectedItems.splice(index, 1);
            } else {
                this.selectedItems.push(item.id);
            }
            console.log(this.selectedItems)
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

.input {
    margin-top: 30px;
}

.custom-text-field {
    width: 150px;
}
</style>
<!-- 刷新同步 -->