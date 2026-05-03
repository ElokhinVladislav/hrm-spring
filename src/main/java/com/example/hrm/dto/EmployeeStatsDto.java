package com.example.hrm.dto;

import java.math.BigDecimal;

public class EmployeeStatsDto {

    private double averageSalary;
    private String topEmployeeName;
    private BigDecimal topEmployeeSalary;

    public EmployeeStatsDto(double averageSalary, String topEmployeeName, BigDecimal topEmployeeSalary) {
        this.averageSalary = averageSalary;
        this.topEmployeeName = topEmployeeName;
        this.topEmployeeSalary = topEmployeeSalary;
    }

    public double getAverageSalary() {
        return averageSalary;
    }

    public String getTopEmployeeName() {
        return topEmployeeName;
    }

    public BigDecimal getTopEmployeeSalary() {
        return topEmployeeSalary;
    }
}