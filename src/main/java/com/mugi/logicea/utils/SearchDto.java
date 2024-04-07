package com.mugi.logicea.utils;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    @NotNull(message = "fieldName cannot be null")
    private String fieldName;
    @NotNull(message = "search value cannot be null")
    private String value;
}
