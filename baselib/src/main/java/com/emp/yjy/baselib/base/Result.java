package com.emp.yjy.baselib.base;

/**
 * 函数执行结果
 *
 * @author Created by LRH
 * @date 2020/12/25 15:18
 */
public class Result<T> {
    private int code = -1;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }
}
