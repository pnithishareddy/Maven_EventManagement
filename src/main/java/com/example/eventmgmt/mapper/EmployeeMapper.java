package com.example.eventmgmt.mapper;

import com.example.eventmgmt.dto.EmployeeRequest;
import com.example.eventmgmt.dto.EmployeeResponse;
import com.example.eventmgmt.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(e.getId(), e.getName(), e.getEmail(), e.getDepartment(),
                e.getSalary(), e.getJoinDate());
    }

    public void updateEntity(Employee e, EmployeeRequest r) {
        e.setName(r.name());
        e.setEmail(r.email());
        e.setDepartment(r.department());
        e.setSalary(r.salary());
        e.setJoinDate(r.joinDate());
    }
}
