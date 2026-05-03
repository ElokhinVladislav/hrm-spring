package com.example.hrm;

import com.example.hrm.dto.EmployeeRequestDto;
import com.example.hrm.model.Employee;
import com.example.hrm.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        EmployeeRequestDto dto = new EmployeeRequestDto();
        dto.setName("Test");
        dto.setPosition("Dev");
        dto.setSalary(BigDecimal.valueOf(1000));
        dto.setHireDate(LocalDate.of(2024, 1, 15));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.position").value("Dev"));
    }

    @Test
    void shouldReturnAllEmployees() throws Exception {
        employeeRepository.save(new Employee(
                "Ivan",
                "Java Developer",
                BigDecimal.valueOf(150000),
                LocalDate.of(2024, 1, 15)
        ));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ivan"));
    }

    @Test
    void shouldReturnNotFoundForMissingEmployee() throws Exception {
        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStatsForEmptyDatabase() throws Exception {
        mockMvc.perform(get("/api/employees/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(0.0))
                .andExpect(jsonPath("$.topEmployeeName").doesNotExist())
                .andExpect(jsonPath("$.topEmployeeSalary").doesNotExist());
    }
}