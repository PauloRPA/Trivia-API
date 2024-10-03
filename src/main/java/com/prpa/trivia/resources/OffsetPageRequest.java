package com.prpa.trivia.resources;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest implements Pageable {

    private static final Sort DEFAULT_SORT = Sort.unsorted();

    private final int limit;
    private final int offset;
    private final Sort sort;

    public OffsetPageRequest(int offset, int limit) {
        this(offset, limit, DEFAULT_SORT);
    }

    public OffsetPageRequest(int offset, int limit, Sort sort) {
        if (limit < 1)
            throw new IllegalArgumentException("Limit must not be less than one!");
        if (offset < 0)
            throw new IllegalArgumentException("Offset index must not be less than zero!");

        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public static OffsetPageRequest of(int offset, int limit) {
        return of(offset, limit, DEFAULT_SORT);
    }

    public static OffsetPageRequest of(int offset, int limit, Sort sort) {
        return new OffsetPageRequest(offset, limit, sort);
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return new OffsetPageRequest((int) (getOffset() + getPageSize()), getPageSize());
    }

    public Pageable previous() {
        return !hasPrevious() ? this :
                new OffsetPageRequest((int) (getOffset() - getPageSize()), getPageSize());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetPageRequest(0, getPageSize());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetPageRequest(getPageSize() * pageNumber, getPageSize());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}