package com.example.pcmall.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class Pagination {
    @Setter
    private Integer currentPage;//当前页数
    private Integer totalPage;//总页数
    private Integer pageSize;//页面大小
    private Integer totalCount;//查询总数

    public Pagination() {
        this.currentPage = 1;
        this.totalPage = 1;
        this.pageSize = 20;
    }

    public Pagination(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean nextPage() {
        if (currentPage <= totalPage) {
            currentPage++;
            return true;
        }
        return false;
    }

    public boolean prePage() {
        if (currentPage > 1) {
            currentPage--;
            return true;
        }
        return false;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        getTotalPage();
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        getTotalPage();
    }

    public Integer getTotalPage() {
        if (totalCount != null) {
            totalPage = totalCount / pageSize;
            if (totalCount % pageSize != 0) {
                totalPage++;
            }
        }
        return totalPage;
    }
}
