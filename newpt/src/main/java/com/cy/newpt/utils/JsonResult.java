package com.cy.newpt.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class JsonResult<E> implements Serializable {

    // 状态码
    private Integer state;
    // 描述信息
    private String message;
    // 数据
    private E data;

    // 构造函数私有化，防止外部随意new
    private JsonResult() {}
    private JsonResult(Integer state, String message, E data) {
        this.data = data;
        this.message = message;
        this.state = state;
    }

    // 静态工厂方法（给controller调用）

    /**
     * 成功只返回状态码
     * @return
     * @param <E>
     */
    public static <E> JsonResult<E> success() {
        return new JsonResult<>(200, "操作成功", null);
    }

    /**
     * 返回成功信息
     * @param message
     * @return
     * @param <E>
     */
    public static <E> JsonResult<E> success(String message) {
        return new JsonResult<>(200, message, null);
    }



    /**
     * 成功
     * @param data 数据
     * @return 只返回数据
     * @param <E>
     */
    public static <E> JsonResult<E> success(E data) {
        return new JsonResult<>(200, "操作成功", data);
    }

    /**
     *
     *
     * 成功
     * @param message 信息
     * @param data 数据
     * @return 信息和数据
     * @param <E>
     */
    public static <E> JsonResult<E> success(String message, E data) {
        return new JsonResult<>(200, message, data);
    }

    /**
     * 失败
     * @param state 状态码
     * @param message 消息
     * @return 状态码和错误消息
     * @param <E>
     */
    public static <E> JsonResult<E> failure(Integer state, String message) {
        return new JsonResult<>(state, message, null);
    }
}
