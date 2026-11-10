# 08 Security - screenshots
1. GET /api/events with no login -> 401 Unauthorized.
2. GET /api/events as user/user123 -> 200.
3. POST /api/events as user/user123 -> 403 Forbidden.
4. POST /api/events as admin/admin123 -> 201.
5. /actuator/metrics as user -> 403, as admin -> 200.
6. Postman Authorization tab (Basic Auth) and the Swagger Authorize dialog.
7. Code: SecurityConfig.java open in an editor (filter chain, users, roles).
