package com.jemmy.framework.utils;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageEntity {
    private final Integer size;

    private final Long totalElements;

    private final Integer totalPages;

    private final Boolean isLast;

    private List<?> content;

    public PageEntity(Page<?> page, List<?> content) {
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isLast = page.isLast();
        this.content = content;
    }

    public Boolean isLast() {
        return isLast;
    }

    public Integer getSize() {
        return size;
    }

    public List<?> getContent() {
        return content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setContent(List<?> content) {
        this.content = content;
    }
}
