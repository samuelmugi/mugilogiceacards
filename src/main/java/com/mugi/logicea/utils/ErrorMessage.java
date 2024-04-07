package com.mugi.logicea.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMessage {
    private String field;
    private String message;
}
