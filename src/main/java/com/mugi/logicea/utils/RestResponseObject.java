package com.mugi.logicea.utils;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestResponseObject<T> {

    private String message;
    private T payload;
    private List<ErrorMessage> errors;


}


