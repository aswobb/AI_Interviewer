<template>
    <div>
        <v-btn icon @click="logout">
            <v-icon>mdi-exit-to-app</v-icon>
        </v-btn>
        <v-container>

            <v-card>
                <v-card-title class="headline">
                    管理者 情報
                </v-card-title>
                <v-card-text>
                    <v-simple-table>
                        <template v-slot:default>
                            <thead>
                                <tr>
                                    <th class="text-left">
                                        Attribute
                                    </th>
                                    <th class="text-left">
                                        Value
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>契約者</td>
                                    <td>{{ user.contractor }}</td>
                                </tr>
                                <tr>
                                    <td>コース名</td>
                                    <td>{{ user.courseName }}</td>
                                </tr>
                                <tr>
                                    <td>残数</td>
                                    <td>{{ user.remainNum }}</td>
                                </tr>
                                <tr>
                                    <td>当月の利用実績</td>
                                    <td>{{ user.usageCount }}</td>
                                </tr>
                            </tbody>
                        </template>
                    </v-simple-table>
                </v-card-text>
                <v-card-actions class="d-flex justify-center">
                    <v-btn color="primary"
                        @click="dialog = true; changePwForm.password = ''; changePwForm.newPassword = ''; changePwForm.reNewPassword = ''">
                        PW再設定
                    </v-btn>
                    <v-btn color="primary" @click="editUser">
                        面接情報——
                    </v-btn>
                    <v-btn color="primary" @click="editCompanyMember">
                        会社員情報
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-container>
        <v-dialog v-model="dialog" max-width="400">
            <v-card-title class="headline">パスワードの変更</v-card-title>
            <v-form ref="form" :model="changePwForm" lazy-validation>
                <v-text-field v-model="changePwForm.password" label="現PW" :rules="[rules.required]"></v-text-field>
                <v-text-field v-model="changePwForm.newPassword"
                    :rules="[rules.required, rules.maxLengh, rules.minLengh]" label="新PW" required></v-text-field>
                <v-text-field v-model="changePwForm.reNewPassword"
                    :rules="[rules.required, rules.maxLengh, rules.minLengh, rules.isDifferent(changePwForm.newPassword)]"
                    label="確認新PW" required></v-text-field>
            </v-form>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="green darken-1" @click="changePw">変更</v-btn>
                <v-btn color="green darken-1" @click="dialog = false">キャンセル</v-btn>
            </v-card-actions>
        </v-dialog>
    </div>
</template>

<script>

export default {
    data() {
        return {
            oldPW: [
                v => !!v || 'パスワードを入力してください',
                v => (v && v === this.user.password) || 'パスワードが間違っています'
            ],
            rules: {
                required: v => !!v || 'パスワードを入力してください',
                maxLengh: v => (v && v.length <= 15) || '文字数は4から15文字まで',
                minLengh: v => (v && v.length >= 4) || '文字数は4から15文字まで',
                isDifferent: (valueToCompare) => {
                    return value => value === valueToCompare || 'パスワードが一致しません'
                }
            },
            user: {
            },
            dialog: false,
            changePwForm: {
                password: '',
                newPassword: '',
                reNewPassword: ''
            }
        };
    },
    created() {
        this.user = this.$store.state.manageInfo
    },
    methods: {
        editCompanyMember() {

            this.$router.push({
                path: '/companymember',
                query: { id: this.user.id }
            });

        },
        logout() {
            this.$router.push('/manage-login')
            this.$store.commit('cleanCache')
        },
        editUser() {
            const token = localStorage.getItem('token');
            console.log(token);
            if (token) {
                let url = 'api/interviewerInfo/list'
                this.axios.get(url, {
                    params: { pageNum: 1, pageSize: 5 },
                    headers: {
                        'token': token
                    },

                }).then((response) => {
                    if (response.data.state == 20000) {
                        //面试者信息赋值
                        this.$store.commit('initInterviewerInfo', response.data)
                    } else {
                        this.$notify.error({
                            message: '面接者情報の取得に失敗しました',
                            type: 'error'
                        });
                    }
                });
                // 导航到编辑用户信息的页面
                this.$router.push({
                    path: '/interview-list',
                    query: { id: this.user.id }
                });

            } else {
                this.$router.push('/manage-login');
                this.$message({
                    message: 'ログインが期限切れです。再度ログインしてください',
                    type: 'warn'
                });
            }
        },
        changePw() {
            if (this.$refs.form.validate()) {
                const token = localStorage.getItem('token');
                let config = {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                };
                let url = 'api/snsUser/updatePassword'
                let data = {
                    id: "3",
                    oldPassword: this.changePwForm.password,
                    newPassword: this.changePwForm.newPassword
                }
                if (token) {
                    this.axios.post(url, data, {
                        headers: {
                            'token': `${token}`
                        }
                    }, config).then((response) => {
                        if (response.data.state == 20000) {
                            this.$message({
                                message: 'パスワード変更に成功しました.',
                                type: 'success'
                            });
                            this.dialog = false
                        } else {
                            this.$notify.error({
                                message: 'パスワード変更に失敗しました.',
                                type: 'error'
                            });
                        }
                    });
                }
            }
        }
    }
}

</script>

<style>
.v-dialog {
    background: #fff;
    height: 350px;
}
</style>