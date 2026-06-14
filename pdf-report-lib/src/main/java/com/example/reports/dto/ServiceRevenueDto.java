package com.example.reports.dto;

import java.math.BigDecimal;

public class ServiceRevenueDto {

    private String serviceName;

    private BigDecimal revenue;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(
            String serviceName
    ) {
        this.serviceName = serviceName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(
            BigDecimal revenue
    ) {
        this.revenue = revenue;
    }
}