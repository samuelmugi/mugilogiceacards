package com.mugi.logicea.cards.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCardRequest {

    @NotNull(message = "name cannot be null")
    private String name;
    @Pattern(regexp = "^#[A-Za-z0-9]{6}$", message = "Field should be 6 alphanumeric characters prefixed with a #")
    @Size(min = 7, max = 7, message = "Field should be 6 alphanumeric characters prefixed with a #")
    @Nullable
    private String color;
    @Nullable
    private String description;
    @NotNull(message = "status cannot be null")
    private String status;
}
