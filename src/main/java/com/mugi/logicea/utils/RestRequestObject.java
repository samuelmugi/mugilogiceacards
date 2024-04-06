package com.mugi.logicea.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestRequestObject<T> {

    private T object;
    private String searchParams;


}
