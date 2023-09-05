package com.sangeng.exception;

import com.sangeng.enums.AppHttpCodeEnum;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/29 16:03
 **/
public class SystemException extends RuntimeException{

    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

}
