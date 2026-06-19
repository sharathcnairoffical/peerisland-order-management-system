package com.peerisland.orders.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RoleDto(
        String roleUniqueId,
        String roleCode,
        String roleName,
        String actions,
        Map<String, String> pages) {
}
