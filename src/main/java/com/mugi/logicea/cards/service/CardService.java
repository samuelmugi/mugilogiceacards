package com.mugi.logicea.cards.service;

import com.mugi.logicea.cards.dtos.CardRequest;
import com.mugi.logicea.cards.dtos.CardStatus;
import com.mugi.logicea.cards.mapper.CardMapper;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.entiy.User;
import com.mugi.logicea.repository.CardRepository;
import com.mugi.logicea.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.mugi.logicea.utils.UtilFunctions.validateNotNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final GenericSpecifications<Card> cardSpecification = new GenericSpecifications<>();
    private final CardRepository cardRepository;

    public ResponseEntity<RestResponseObject> createCard(CardRequest request) {
        Card card = CardMapper.INSTANCE.requestToCardEntity(request);
        card.setStatus(CardStatus.TO_DO);
        cardRepository.save(card);
        return new RestResponse(
                RestResponseObject.builder().message("Card Created Successfully").build(),
                HttpStatus.OK);
    }

    public ResponseEntity<RestResponseObject> updateCard(Long id, CardRequest request) {
        Optional<Card> existingCard = cardRepository.findById(id);
        if (existingCard.isPresent()) {
            Card card = CardMapper.INSTANCE.requestToCardEntity(request);
            card.setId(id);
            cardRepository.save(card);
            return new RestResponse(
                    RestResponseObject.builder().message("Card Updated Successfully").build(),
                    HttpStatus.OK);
        } else {
            return new RestResponse(
                    RestResponseObject.builder().message("Card not found!").build(),
                    HttpStatus.NOT_FOUND);

        }
    }

    public ResponseEntity<Card> fetchCard(SearchDto searchDto) {
        List<ErrorMessage> validateObj = validateNotNull(searchDto);
        if (ObjectUtils.isNotEmpty(validateObj)) {
            return new ResponseEntity(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            Specification<Card> cardSpec = cardSpecification.findByFieldAndValue(searchDto);
            Optional<Card> card = cardRepository.findOne(cardSpec);
            if (card.isEmpty()) new ResponseEntity(
                    RestResponseObject.builder().message("No card found!!").build(),
                    HttpStatus.NOT_FOUND);
            return new ResponseEntity(
                    RestResponseObject.builder().message("Fetched Successfully")
                            .payload(card)
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("fetchCard Exception:-" + e.getMessage());
            return new ResponseEntity(
                    RestResponseObject.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Page<Card>> fetchPaginatedCardList(SearchDto searchDto, Pageable pageable) {
        try {
            Specification<Card> cardSpec = Specification.where(null);
            if (ObjectUtils.isNotEmpty(searchDto.getFieldName())
                    && ObjectUtils.isNotEmpty(searchDto.getValue())) {
                cardSpec = cardSpecification.findByFieldAndValue(searchDto);
            }
            Page<Card> page = cardRepository.findAll(cardSpec, pageable);

            return new ResponseEntity(
                    RestResponseObject.builder().message("Fetched Successfully")
                            .payload(page)
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("fetchPaginatedCardList Exception:-" + e.getMessage());
            return new ResponseEntity(
                    RestResponseObject.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RestResponseObject> deleteCard(Long id) {
        Optional<Card> existingCard = cardRepository.findById(id);
        if (existingCard.isPresent()) {

            cardRepository.delete(existingCard.get());
            return new RestResponse(
                    RestResponseObject.builder().message("Card Deleted Successfully").build(),
                    HttpStatus.OK);
        } else {
            return new RestResponse(
                    RestResponseObject.builder().message("Card not found!").build(),
                    HttpStatus.NOT_FOUND);

        }
    }
}
