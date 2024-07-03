package com.app.sns.aiproduct.constant;

public enum ServiceCodeEnum {
    OK(20000, "成功"),
    ERR_BAN_REQUEST(40000, "リクエストパラメータのフォーマットが誤っています。"),
    ERR_PAR_EMPTY(40001, "リクエストパラメータが空です。"),
    ERR_FILE_EMPTY(40009, "ファイルが空です。"),
    ERR_PASSWORD_ERROR(40002, "パスワードが正しくないです。"),
    ERR_USER_NOT_FOUND(40003, "ユーザー名が見つかりません。"),
    ERR_USER_DISABLE(40004, "ユーザー名が無効です。"),
    ERR_USER_EXPIRED(40005, "ユーザー有効期限切りました。"),
    ERR_NOT_FOUND(40400, "関連するリソースまたはデータが見つかりません。"),
    ERR_DISABLE(40401, "関連するリソースが無効です。"),
    ERR_NOT_ALLOWED(40402, "このリソースにアクセスする権限がありません。"),
    ERR_CONFLICT(40900, "データの競合があります。例えば、重複が許されないデータの追加を試みた場合です。"),
    ERR_INTERNAL_SERVICE_ERROR(50000, "サーバー内部エラー、通常は未定義の例外の種類です。"),
    ERR_INSERT(50001, "データの挿入時にエラーが発生しました。"),
    ERR_UPDATE(50002, "データの変更時にエラーが発生しました。"),
    ERR_DELETE(50003, "データの削除時にエラーが発生しました。"),
    ERR_TOKEN(50401, "ユーザー認証に失敗しました。"),
    ERR_READ_FILE(50402, "履歴書の解析に失敗しました。"),
    INVALID_OLD_PASSWORD(60001, "古いパスワードが正しくありません。"),
    INSUFFICIENT_BALANCE(70001, "残高が不足です。");


    private final Integer code;
    private final String message;

    ServiceCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return "DataDictionary{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
