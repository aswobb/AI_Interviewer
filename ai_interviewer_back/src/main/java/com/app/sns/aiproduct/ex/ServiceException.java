package com.app.sns.aiproduct.ex;

import lombok.Getter;
/**
 * 异常处理类
 *
 * @author 張
 * @version 0.0.1
 */
@Getter
public class ServiceException extends RuntimeException {
    private Integer state;

    public ServiceException(Integer state, String message) {
        super(message);
        this.state = state;
    }
}
