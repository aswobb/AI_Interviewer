package com.app.sns.aiproduct.web;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一的响应结果类型
 *
 * @author 張
 * @version 0.0.1
 */
@Data
public class JsonResult<T> implements Serializable {
    /**
     * 业务状态码
     */
    private Integer state;
    /**
     * 消息
     */
    private String message;
    /**
     * 数据
     */
    private T data; // T(Type)或者E(Element)、K(Key)、V(Value)

    public static JsonResult ok() {
        return ok(null);
    }

    public static <T> JsonResult<T> ok(T data) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setState(ServiceCodeEnum.OK.getCode());
        jsonResult.setData(data);
        return jsonResult;
    }

    public static JsonResult<Void> fail(ServiceException e) {
        return fail(e.getState(), e.getMessage());
    }

    public static JsonResult<Void> fail(Integer state, String message) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setState(state);
        jsonResult.setMessage(message);
        return jsonResult;
    }
}
