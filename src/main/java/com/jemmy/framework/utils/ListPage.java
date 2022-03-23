package com.jemmy.framework.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.function.Function;

public class ListPage<T> implements Page<T> {

    private List<T> content = new ArrayList<>();
    private Pageable pageable;
    private long total = 0;
    private Boolean last = false;

    /**
     * Constructor of {@code PageImpl}.
     *
     * @param content the content of this page, must not be {@literal null}.
     * @param pageable the paging information, can be {@literal null}.
     */
    public ListPage(List<T> content, Pageable pageable) {

        if (null == content) {
            throw new IllegalArgumentException("Content must not be null!");
        }

        Integer start = pageable.getPageNumber();
        Integer size = pageable.getPageSize();

        List<T> list;

        if (start == 0 && size > content.size()) {
            list = content;
        } else {
            list = new ArrayList<>();
            for (int i = start * size; i < start * size + size && i < content.size(); i++) {
                list.add(content.get(i));
            }
        }

        this.content = list;
        this.total = content.size();
        this.pageable = pageable;
        this.last = start * size + size > content.size();
    }

    public ListPage(List<T> content, Pageable pageable, Long total) {
        this.content = content;
        this.pageable = pageable;
        this.total = total;
        this.last = (long) pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize() > total;
    }

    public ListPage() {
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#getNumber()
     */
    public int getNumber() {
        return pageable == null ? 0 : pageable.getPageNumber();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#getSize()
     */
    public int getSize() {
        return pageable == null ? 0 : pageable.getPageSize();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#getTotalPages()
     */
    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#getNumberOfElements()
     */
    public int getNumberOfElements() {
        return content.size();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#getTotalElements()
     */
    public long getTotalElements() {
        return total;
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> function) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#hasPreviousPage()
     */
    public boolean hasPreviousPage() {
        return getNumber() > 0;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#isFirstPage()
     */
    public boolean isFirstPage() {
        return !hasPreviousPage();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#hasNextPage()
     */
    public boolean hasNextPage() {
        return getNumber() + 1 < getTotalPages();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#isLastPage()
     */
    public boolean isLastPage() {
        return !hasNextPage();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#nextPageable()
     */
    public Pageable nextPageable() {
        return hasNextPage() ? pageable.next() : null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#previousOrFirstPageable()
     */
    public Pageable previousPageable() {

        if (hasPreviousPage()) {
            return pageable.previousOrFirst();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#iterator()
     */
    public Iterator<T> iterator() {
        return content.iterator();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#getContent()
     */
    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#hasContent()
     */
    public boolean hasContent() {
        return !content.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Page#getSort()
     */
    public Sort getSort() {
        return pageable == null ? null : pageable.getSort();
    }

    @Override
    public boolean isFirst() {
        return pageable.getPageNumber() == 0;
    }

    @Override
    public boolean isLast() {
        return this.last;
    }

    @Override
    public boolean hasNext() {
        return !this.last;
    }

    @Override
    public boolean hasPrevious() {
        return !isFirst();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        String contentType = "UNKNOWN";

        if (content.size() > 0) {
            contentType = content.get(0).getClass().getName();
        }

        return String.format("Page %s of %d containing %s instances", getNumber(), getTotalPages(), contentType);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ListPage<?>)) {
            return false;
        }

        ListPage<?> that = (ListPage<?>) obj;

        boolean totalEqual = this.total == that.total;
        boolean contentEqual = this.content.equals(that.content);
        boolean pageableEqual = Objects.equals(this.pageable, that.pageable);

        return totalEqual && contentEqual && pageableEqual;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;

        result = 31 * result + (int) (total ^ total >>> 32);
        result = 31 * result + (pageable == null ? 0 : pageable.hashCode());
        result = 31 * result + content.hashCode();

        return result;
    }
}
