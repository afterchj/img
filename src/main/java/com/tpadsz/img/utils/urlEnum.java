package com.tpadsz.img.utils;

/**
 * Created by pan.sun on 2016/12/8.
 */
public enum urlEnum {
    Url_BYS("白云山", "xxxxxxxxxx"),
    Url_Local("本地", "C:\\Users\\pan.sun\\IdeaProjects\\img.api\\target");
    urlEnum(String code, String value) {
        this.value = value;
        this.code = code;
    }
    private String value;
    private String code;

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

}
