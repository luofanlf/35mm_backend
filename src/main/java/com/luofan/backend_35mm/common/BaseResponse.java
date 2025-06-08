package com.luofan.backend_35mm.common;

import lombok.Data;
import com.luofan.backend_35mm.exception.ErrorCode;
import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {


    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

