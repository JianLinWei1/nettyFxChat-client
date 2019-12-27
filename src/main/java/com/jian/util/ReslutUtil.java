package com.jian.util;

import java.io.Serializable;

/**
 * @auther JianLinWei
 * @date 2019-12-27 16:00
 */
public class ReslutUtil implements Serializable {
    private  int code;
    private  String cmd ;
    private  Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
