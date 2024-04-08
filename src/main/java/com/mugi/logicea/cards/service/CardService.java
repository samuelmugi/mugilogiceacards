package com.mugi.logicea.cards.service;

import com.mugi.logicea.cards.dtos.CardStatus;
import com.mugi.logicea.cards.dtos.CreateCardRequest;
import com.mugi.logicea.cards.dtos.UpdateCardRequest;
import com.mugi.logicea.cards.mapper.CardMapper;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.repository.CardRepository;
import com.mugi.logicea.uac.dtos.Role;
import com.mugi.logicea.utils.ErrorMessage;
import com.mugi.logicea.utils.GenericSpecifications;
import com.mugi.logicea.utils.RestResponseObject;
import com.mugi.logicea.utils.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mugi.logicea.utils.UtilFunctions.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final GenericSpecifications<Card> cardSpecification = new GenericSpecifications<>();
    private final CardRepository cardRepository;

    public ResponseEntity<RestResponseObject<Card>> createCard(CreateCardRequest request) {
        Card card = CardMapper.INSTANCE.createRequestToCardEntity(request);
        List<ErrorMessage> validateObj = validateNotNull(request);
        if (ObjectUtils.isNotEmpty(validateObj)) {

            return new ResponseEntity(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        card.setStatus(CardStatus.TO_DO);
        var currentUser = getCurrentUser();
        card.setCreateDate(Date.from(Instant.now()));
        card.setModifiedDate(Date.from(Instant.now()));
        card.setCreateBy(currentUser);
        card.setModifiedBy(currentUser);
        Card newCard = cardRepository.save(card);
        return new ResponseEntity(
                RestResponseObject.builder().message("Card Created Successfully").payload(newCard).build(),
                HttpStatus.OK);
    }


    public ResponseEntity<RestResponseObject<Card>> updateCard(Long id, UpdateCardRequest request) {
        List<ErrorMessage> validateObj = validateNotNull(request);
        if (ObjectUtils.isNotEmpty(validateObj)) {

            return new ResponseEntity(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<Card> existingCard = cardRepository.findById(id);
        if (existingCard.isPresent()) {
            Card card = CardMapper.INSTANCE.updateCardRequestToCardEntity(request);
            card.setId(id);
            card.setCreateDate(existingCard.get().getCreateDate());
            card.setCreateBy(existingCard.get().getCreateBy());
            var currentUser = getCurrentUser();
            if (ObjectUtils.notEqual(currentUser, existingCard.get().getCreateBy())) {
                return new ResponseEntity(
                        RestResponseObject.builder().message("Access denied!!").build(),
                        HttpStatus.BAD_REQUEST);
            }
            card.setModifiedDate(Date.from(Instant.now()));
            card.setModifiedBy(currentUser);
            Card updatedCard = cardRepository.save(card);
            return new ResponseEntity(
                    RestResponseObject.builder().message("Card Updated Successfully").payload(updatedCard).build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity(
                    RestResponseObject.builder().message("Card not found!").build(),
                    HttpStatus.NOT_FOUND);

        }
    }

    public ResponseEntity<RestResponseObject<Card>> fetchCard(SearchDto searchDto) {
        List<ErrorMessage> validateObj = validateNotNull(searchDto);
        if (ObjectUtils.isNotEmpty(validateObj)) {
            return new ResponseEntity(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            Specification<Card> cardSpec = cardSpecification.findByFieldAndValue(searchDto);
            Optional<Card> card = cardRepository.findOne(cardSpec);
            if (card.isEmpty()) return new ResponseEntity(
                    RestResponseObject.builder().message("No card found!!").build(),
                    HttpStatus.NOT_FOUND);
            if (validateUserHasNoAccess(card.get().getCreateBy())) {
                return new ResponseEntity(
                        RestResponseObject.builder().message("Access denied!!").build(),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(
                    RestResponseObject.builder().message("Fetched Successfully")
                            .payload(card.get())
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
            String userRole = getCurrentUserRole();
            if (userRole.equals(Role.ROLE_MEMBER.toString())) {
                cardSpec = cardSpec.and(cardSpecification.findByFieldAndValue(SearchDto.builder()
                        .fieldName("createBy")
                        .value(getCurrentUser())
                        .build()));
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
        var currentUser = getCurrentUser();
        if (ObjectUtils.notEqual(currentUser, existingCard.get().getCreateBy())) {
            return new ResponseEntity(
                    RestResponseObject.builder().message("Access denied!!").build(),
                    HttpStatus.BAD_REQUEST);
        }
        if (existingCard.isPresent()) {

            cardRepository.delete(existingCard.get());
            return new ResponseEntity(
                    RestResponseObject.builder().message("Card Deleted Successfully").build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity(
                    RestResponseObject.builder().message("Card not found!").build(),
                    HttpStatus.NOT_FOUND);

        }
    }

    boolean validateUserHasNoAccess(String createdBy) {
        String userRole = getCurrentUserRole();
        if (ObjectUtils.equals(userRole, Role.ROLE_ADMIN.toString())) return false;
        var currentUser = getCurrentUser();
        return ObjectUtils.notEqual(currentUser, createdBy);

    }
}
