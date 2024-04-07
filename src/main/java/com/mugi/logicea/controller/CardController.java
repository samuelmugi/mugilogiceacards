package com.mugi.logicea.controller;


import com.mugi.logicea.cards.dtos.CardRequest;
import com.mugi.logicea.cards.service.CardService;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.uac.dtos.*;
import com.mugi.logicea.utils.RestResponseObject;
import com.mugi.logicea.utils.SearchDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/card")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CardController {
    private final CardService cardService;

    @PostMapping("/createCard")
    public ResponseEntity<RestResponseObject> createCard(@RequestBody CardRequest request) {
        return cardService.createCard(request);
    }

    @PostMapping("/updateCard/{id}")
    public ResponseEntity<RestResponseObject> updateCard(@PathVariable Long id, @RequestBody CardRequest request) {
        return cardService.updateCard(id,request);
    }

    @DeleteMapping("/deleteCard/{id}")
    public ResponseEntity<RestResponseObject> deleteCard(@PathVariable Long id) {
        return cardService.deleteCard(id);
    }

    @GetMapping(path = "/search", produces = "application/json")
    ResponseEntity<Card> fetchCard(SearchDto searchDto) {
        return cardService.fetchCard(searchDto);
    }

    @GetMapping(path = "/list", produces = "application/json")
    ResponseEntity<Page<Card>> fetchPaginatedCardList(SearchDto searchDto, Pageable pageable) {
        return cardService.fetchPaginatedCardList(searchDto, pageable);
    }
}
