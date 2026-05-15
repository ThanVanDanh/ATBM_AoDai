package util;

public class PaginationUtils {

    public static class PageInfo {
        private final int currentPage;
        private final int totalPages;
        private final int offset;

        public PageInfo(int currentPage, int totalPages, int offset) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.offset = offset;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getOffset() {
            return offset;
        }
    }

    public static PageInfo calculate(String pageParam, int totalItems, int pageSize) {
        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }
        int offset = (page - 1) * pageSize;

        return new PageInfo(page, totalPages, offset);
    }
}
