package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.EmployeeRequest;
import com.example.eventmgmt.dto.EmployeeResponse;
import com.example.eventmgmt.entity.Employee;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.exception.ResourceNotFoundException;
import com.example.eventmgmt.mapper.EmployeeMapper;
import com.example.eventmgmt.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock EmployeeRepository repo;
    @Spy EmployeeMapper mapper = new EmployeeMapper();
    @InjectMocks EmployeeService service;

    private EmployeeRequest request(String email) {
        return new EmployeeRequest("Ravi", email, "IT", new BigDecimal("50000"), LocalDate.of(2024, 1, 15));
    }

    @Test
    void create_savesEmployee_whenEmailIsNew() {
        when(repo.existsByEmail("ravi@x.com")).thenReturn(false);
        when(repo.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        EmployeeResponse res = service.create(request("ravi@x.com"));

        assertEquals("Ravi", res.name());
        assertEquals("IT", res.department());
        verify(repo).save(any(Employee.class));
    }

    @Test
    void create_throws_whenEmailAlreadyExists() {
        when(repo.existsByEmail("ravi@x.com")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.create(request("ravi@x.com")));
        verify(repo, never()).save(any());
    }

    @Test
    void get_throws_whenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(99L));
    }

    @Test
    void update_throws_whenEmailBelongsToAnotherEmployee() {
        Employee existing = new Employee();
        existing.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByEmailAndIdNot("taken@x.com", 1L)).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.update(1L, request("taken@x.com")));
    }
}
