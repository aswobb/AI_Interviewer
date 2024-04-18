package com.app.sns.aiproduct.constant;

public enum DataDictionary {

    ROLE_ADMINISTRATOR("0", "0", "管理者"),
    ROLE_STAFF("1", "1", "普通の社員"),
    ROLE_CONTRACT("2", "2", "契約管理者"),
    ROLE_INTERVIEWERS("3", "3", "面接者"),

    BILLING_COUSE_CUSTOM("6", "その他", "Custom");
    private final String key;
    private final String value;
    private final String comment;

    DataDictionary(String key, String value, String comment) {
        this.key = key;
        this.value = value;
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "DataDictionary{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
