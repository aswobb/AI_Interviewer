package com.app.sns.aiproduct.ex;

import com.app.sns.aiproduct.web.JsonResult;
import com.app.sns.aiproduct.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理的配置类
 *
 * @author 張
 * @version 0.0.1
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    public GlobalExceptionHandler() {
        log.debug("创建统一处理异常的类：GlobalExceptionHandler");
    }

    @ExceptionHandler
    public JsonResult<Void> handleServiceException(ServiceException e) {
        return JsonResult.fail(e);
    }

    @ExceptionHandler
    public JsonResult<Void> handleThrowable(Throwable e) {
        log.debug("处理了Throwable");
        e.printStackTrace();
        String message = "运行过程中出现意外错误,请联系系统管理员!";
        return JsonResult.fail(ServiceCode.ERR_INTERNAL_SERVICE_ERROR, message);
    }

    @ExceptionHandler
    public JsonResult<Void> handleBindException(BindException e) {
        log.debug("处理了BindException:{}", e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrors = e.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String message = fieldError.getDefaultMessage();
            stringBuilder.append(message);
        }
        return JsonResult.fail(ServiceCode.ERR_BAN_REQUEST, stringBuilder.toString());
    }
}
