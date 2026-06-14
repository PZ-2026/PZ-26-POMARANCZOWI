package com.example.reports.dto;
import java.util.List;
import java.math.BigDecimal;

public class RevenueReportDto {

    private String period;

    private Integer appointmentsCount;

    private BigDecimal totalRevenue;

    private List<ServiceRevenueDto> servicesRevenue;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public List<ServiceRevenueDto> getServicesRevenue() {
    return servicesRevenue;
}

public void setServicesRevenue(
        List<ServiceRevenueDto> servicesRevenue
) {
    this.servicesRevenue =
            servicesRevenue;
}
}