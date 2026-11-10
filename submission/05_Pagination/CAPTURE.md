# 05 Pagination - screenshots
1. GET /api/events?page=0&size=1 -> response showing content, page, size, totalElements, totalPages.
2. GET /api/events?page=1&size=1 -> second page.
3. GET /api/employees?page=0&size=2 -> totalPages > 1.
4. GET /api/events/1/registrations?page=0&size=5.
5. Console log or SQL showing the paged query (offset/limit).
