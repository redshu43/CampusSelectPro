package com.cy.newpt.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页视图对象，封装分页对象的全部信息
 * @param <T> 对象类型
 */
@Data
public class PageVO<T> implements Serializable {
    private long pageNum;   // 当前页码
    private long pageSize;  // 每条页数
    private long total;     // 总条数
    private long pages;     // 总页数
    private List<T> list;   // 当前页的数据列表

    // 为所有字段准备getter和setter
    // 会自动有一个无参构造
}
