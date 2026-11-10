package com.example.eventmgmt.controller;

import com.example.eventmgmt.dto.EmployeeRequest;
import com.example.eventmgmt.dto.EmployeeResponse;
import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Employees")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService service;

    @Operation(summary = "Create an employee (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest r) {
        EmployeeResponse created = service.create(r);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get an employee by id")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary = "List employees: ?page=0&size=5&sort=salary,desc&department=IT")
    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> list(
            @RequestParam(required = false) String department,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.list(department, pageable));
    }

    @Operation(summary = "JPQL search by name or department")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<EmployeeResponse>> search(
            @RequestParam String keyword, @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.search(keyword, pageable));
    }

    @Operation(summary = "Update an employee (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest r) {
        return ResponseEntity.ok(service.update(id, r));
    }

    @Operation(summary = "Delete an employee (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
