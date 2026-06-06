package com.example.reports.dto;

import java.util.List;

public class ServicePopularityReportDto {

    private Integer totalServices;

    private Integer uniqueServices;

    private String mostPopularService;

    private List<PopularServiceDto> services;

    public Integer getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(Integer totalServices) {
        this.totalServices = totalServices;
    }

    public Integer getUniqueServices() {
        return uniqueServices;
    }

    public void setUniqueServices(Integer uniqueServices) {
        this.uniqueServices = uniqueServices;
    }

    public String getMostPopularService() {
        return mostPopularService;
    }

    public void setMostPopularService(String mostPopularService) {
        this.mostPopularService = mostPopularService;
    }

    public List<PopularServiceDto> getServices() {
        return services;
    }

    public void setServices(List<PopularServiceDto> services) {
        this.services = services;
    }
}