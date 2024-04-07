package com.mugi.logicea.cards.mapper;


import com.mugi.logicea.cards.dtos.CardRequest;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.entiy.User;
import com.mugi.logicea.uac.dtos.SignUpRequest;
import com.mugi.logicea.uac.dtos.SignUpUpdateRequest;
import com.mugi.logicea.uac.dtos.UserResponse;
import com.mugi.logicea.utils.StringTrimmer;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
uses = StringTrimmer.class)
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    Card requestToCardEntity(CardRequest cardRequest);

 }
