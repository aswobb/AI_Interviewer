package com.app.sns.aiproduct.ex;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
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

    public ServiceException(ServiceCodeEnum serviceCodeEnum) {
        super(serviceCodeEnum.getMessage());
        this.state = serviceCodeEnum.getCode();
    }
}
