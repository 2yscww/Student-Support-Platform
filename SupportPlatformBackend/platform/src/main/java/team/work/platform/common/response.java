package team.work.platform.common;

import lombok.Data;

// ? 自定义返回的json格式

@Data
public class Response<T> {

    private int code;
    private String msg;
    private T data;

    public Response() {
    }

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ? 静态方法构造

    public static <T> Response<T> Success(T data, String msg) {
        return new Response<>(200, msg, data);
    }

    public static <T> Response<T> Fail(T data, String msg) {
        return new Response<>(400, msg, data);
    }

    public static <T> Response<T> Error(T data, String msg) {
        return new Response<>(500, msg, data);
    }



}
