package com.example.hrm.controller;

import com.example.hrm.dto.EmployeeRequestDto;
import com.example.hrm.dto.EmployeeResponseDto;
import com.example.hrm.dto.EmployeeStatsDto;
import com.example.hrm.exception.EmployeeNotFoundException;
import com.example.hrm.model.Employee;
import com.example.hrm.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponseDto> getAll() {
        return employeeService.getAllEmployees()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @GetMapping("/page")
    public Page<EmployeeResponseDto> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortParams[0])
        );

        return employeeService.getEmployeesPage(pageable)
                .map(this::toResponseDto);
    }

    @GetMapping("/{id}")
    public EmployeeResponseDto getById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return toResponseDto(employee);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto create(@Valid @RequestBody EmployeeRequestDto requestDto) {
        Employee employee = new Employee(
                requestDto.getName(),
                requestDto.getPosition(),
                requestDto.getSalary(),
                requestDto.getHireDate()
        );

        Employee savedEmployee = employeeService.createEmployee(employee);

        return toResponseDto(savedEmployee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/position/{position}")
    public List<EmployeeResponseDto> getByPosition(@PathVariable String position) {
        return employeeService.getByPosition(position)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @GetMapping("/stats")
    public EmployeeStatsDto getStats() {
        double averageSalary = employeeService.getAverageSalary();

        return employeeService.getTopEmployee()
                .map(employee -> new EmployeeStatsDto(
                        averageSalary,
                        employee.getName(),
                        employee.getSalary()
                ))
                .orElse(new EmployeeStatsDto(
                        averageSalary,
                        null,
                        null
                ));
    }

    private EmployeeResponseDto toResponseDto(Employee employee) {
        return new EmployeeResponseDto(
                employee.getId(),
                employee.getName(),
                employee.getPosition(),
                employee.getSalary(),
                employee.getHireDate()
        );
    }
}