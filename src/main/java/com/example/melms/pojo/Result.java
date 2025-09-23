package com.example.melms.pojo;

public class Result {
    private final String code;
    private final String message;
    private final Object data;
    private Result(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static Result success(String message, Object data) {
        return new Result("000", message, data);
    }
    public static Result fail(String code, String message, Object data) {
        return new Result(code, message, data);
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public Object getData() {
        return data;
    }
}
