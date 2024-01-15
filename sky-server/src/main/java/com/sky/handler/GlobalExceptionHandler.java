package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }


    /**
     * 处理sql异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry '小智' for key 'employee.idx_username'
        //1.获取异常信息
        String message = ex.getMessage();

        //判断异常信息中是否包含 Duplicate entry
        if (message.contains("Duplicate entry")){
            //将这个报错信息封装在数组中
            String[] split = message.split(" ");
            String username = split[2];
            //String msg = username + "已存在";
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else {
            //return Result.error("未知错误");
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }
}
