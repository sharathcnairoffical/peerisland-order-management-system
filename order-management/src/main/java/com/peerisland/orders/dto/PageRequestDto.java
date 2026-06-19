package com.peerisland.orders.dto;

import lombok.Builder;

@Builder
public record PageRequestDto(
        int page,
        int size) {

    public PageRequestDto {
        if (page <= 0) {
            page = 0;
      }
        if (size <= 0) {
            size = 5;
        }
    }
}
