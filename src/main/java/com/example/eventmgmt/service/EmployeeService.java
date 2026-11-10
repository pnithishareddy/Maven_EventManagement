package com.example.eventmgmt.service;

import com.example.eventmgmt.aop.Audited;
import com.example.eventmgmt.dto.EmployeeRequest;
import com.example.eventmgmt.dto.EmployeeResponse;
import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.entity.Employee;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.exception.ResourceNotFoundException;
import com.example.eventmgmt.mapper.EmployeeMapper;
import com.example.eventmgmt.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Employee CRUD with pagination, sorting, search and a unique email constraint. */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repo;
    private final EmployeeMapper mapper;

    @Audited("'Created employee ' + #result.id() + ' (' + #result.name() + ')'")
    @Transactional(rollbackFor = Exception.class)
    public EmployeeResponse create(EmployeeRequest r) {
        if (repo.existsByEmail(r.email()))
            throw new DuplicateResourceException("Employee email already exists: " + r.email());
        Employee e = new Employee();
        mapper.updateEntity(e, r);
        Employee saved = repo.save(e);
        log.info("Employee created id={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> list(String department, Pageable pageable) {
        Page<Employee> page = (department == null || department.isBlank())
                ? repo.findAll(pageable) : repo.findByDepartmentIgnoreCase(department, pageable);
        return PageResponse.from(page, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> search(String keyword, Pageable pageable) {
        return PageResponse.from(repo.search(keyword, pageable), mapper::toResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    public EmployeeResponse update(Long id, EmployeeRequest r) {
        Employee e = find(id);
        if (repo.existsByEmailAndIdNot(r.email(), id))
            throw new DuplicateResourceException("Employee email already exists: " + r.email());
        mapper.updateEntity(e, r);
        log.info("Employee updated id={}", id);
        return mapper.toResponse(repo.save(e));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        repo.delete(find(id));
        log.info("Employee deleted id={}", id);
    }

    private Employee find(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }
}
