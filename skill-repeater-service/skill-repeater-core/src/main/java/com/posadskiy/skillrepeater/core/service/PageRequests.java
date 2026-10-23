package com.posadskiy.skillrepeater.core.service;

public final class PageRequests {

    public static final int MAX_PAGE_SIZE = 100;
    public static final int DUE_LIMIT = 10;

    private PageRequests() {
    }

    public static int page(int page) {
        return Math.max(page, 0);
    }

    public static int size(int size) {
        if (size < 1) {
            return 20;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    public static int dueLimit(int limit) {
        if (limit < 1) {
            return DUE_LIMIT;
        }
        return Math.min(limit, DUE_LIMIT);
    }
}
