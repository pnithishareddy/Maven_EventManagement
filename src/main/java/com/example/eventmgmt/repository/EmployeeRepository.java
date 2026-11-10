package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    Page<Employee> findByDepartmentIgnoreCase(String department, Pageable pageable);

    @Query("select e from Employee e where lower(e.name) like lower(concat('%', :keyword, '%')) "
         + "or lower(e.department) like lower(concat('%', :keyword, '%'))")
    Page<Employee> search(@Param("keyword") String keyword, Pageable pageable);
}
