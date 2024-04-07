package com.mugi.logicea.cards.mapper;


import com.mugi.logicea.cards.dtos.CreateCardRequest;
import com.mugi.logicea.cards.dtos.UpdateCardRequest;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.utils.StringTrimmer;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
uses = StringTrimmer.class)
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    Card createRequestToCardEntity(CreateCardRequest createCardRequest);
    Card updateCardRequestToCardEntity(UpdateCardRequest updateCardRequest);

 }
