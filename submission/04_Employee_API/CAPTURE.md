# 04 Employee API - screenshots (Postman folder '06 Employee API')
1. POST /api/employees -> 201 Created (show URL, body, status, response, Location header).
2. GET /api/employees/1 -> 200.
3. GET /api/employees -> 200 (list).
4. PUT /api/employees/1 -> 200 (updated values).
5. DELETE /api/employees/3 -> 204 No Content.
6. Duplicate email -> 409. Validation error -> 400. Not found -> 404.
7. Unit tests: EmployeeServiceTest passing in the mvn test output.
