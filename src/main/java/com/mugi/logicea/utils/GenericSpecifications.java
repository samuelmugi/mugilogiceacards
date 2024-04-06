package com.mugi.logicea.utils;

import org.springframework.data.jpa.domain.Specification;

public class GenericSpecifications<T> {


    public Specification<T> findByFieldAndValue(SearchDto searchDto) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(searchDto.getFieldName()), searchDto.getValue());
    }
 }