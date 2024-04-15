package com.app.sns.aiproduct.ex;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.web.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * グローバルな例外処理の設定クラス
 *
 * @author 張
 * @version 0.0.1
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    public GlobalExceptionHandler() {
        log.debug("グローバルな例外処理クラスを作成：GlobalExceptionHandler");
    }

    @ExceptionHandler
    public JsonResult<Void> handleServiceException(ServiceException e) {
        return JsonResult.fail(e);
    }

    @ExceptionHandler
    public JsonResult<Void> handleThrowable(Throwable e) {
        log.debug("Throwableを処理しました");
        e.printStackTrace();
        String message = "実行中に予期せぬエラーが発生しました。システム管理者にお問い合わせください。";
        return JsonResult.fail(ServiceCodeEnum.ERR_INTERNAL_SERVICE_ERROR.getCode(), message);
    }

    @ExceptionHandler
    public JsonResult<Void> handleBindException(BindException e) {
        log.debug("BindExceptionを処理しました:{}", e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrors = e.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String message = fieldError.getDefaultMessage();
            stringBuilder.append(message);
        }
        return JsonResult.fail(ServiceCodeEnum.ERR_BAN_REQUEST.getCode(), stringBuilder.toString());
    }
}
