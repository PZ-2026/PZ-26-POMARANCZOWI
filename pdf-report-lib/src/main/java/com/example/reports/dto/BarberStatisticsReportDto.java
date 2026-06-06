package com.example.reports.dto;

import java.math.BigDecimal;

public class BarberStatisticsReportDto {

    private String barberName;

    private Integer appointmentsCount;

    private BigDecimal totalRevenue;

    private BigDecimal averageRevenuePerVisit;

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public Integer getAppointmentsCount() {
        return appointmentsCount;
    }

    public void setAppointmentsCount(Integer appointmentsCount) {
        this.appointmentsCount = appointmentsCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAverageRevenuePerVisit() {
        return averageRevenuePerVisit;
    }

    public void setAverageRevenuePerVisit(BigDecimal averageRevenuePerVisit) {
        this.averageRevenuePerVisit = averageRevenuePerVisit;
    }
}