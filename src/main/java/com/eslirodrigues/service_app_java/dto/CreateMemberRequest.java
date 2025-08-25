package com.eslirodrigues.service_app_java.dto;

import com.eslirodrigues.service_app_java.entity.ServiceType;

import java.time.LocalDate;

public record CreateMemberRequest(
    String name,
    String email,
    LocalDate birthDate,
    String photo,
    ServiceType serviceType
) {}