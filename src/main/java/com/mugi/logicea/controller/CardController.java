package com.mugi.logicea.controller;


import com.mugi.logicea.cards.dtos.CreateCardRequest;
import com.mugi.logicea.cards.dtos.UpdateCardRequest;
import com.mugi.logicea.cards.service.CardService;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.utils.RestResponseObject;
import com.mugi.logicea.utils.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/card")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CardController {
    private final CardService cardService;

    @PostMapping("/createCard")
    public ResponseEntity<RestResponseObject<Card>> createCard(@RequestBody CreateCardRequest request) {
        return cardService.createCard(request);
    }

    @PostMapping("/updateCard/{id}")
    public ResponseEntity<RestResponseObject<Card>>  updateCard(@PathVariable Long id, @RequestBody UpdateCardRequest request) {
        return cardService.updateCard(id,request);
    }

    @DeleteMapping("/deleteCard/{id}")
    public ResponseEntity<RestResponseObject> deleteCard(@PathVariable Long id) {
        return cardService.deleteCard(id);
    }

    @GetMapping(path = "/search", produces = "application/json")
    ResponseEntity<RestResponseObject<Card>>  fetchCard(SearchDto searchDto) {
        return cardService.fetchCard(searchDto);
    }

    @GetMapping(path = "/list", produces = "application/json")
    ResponseEntity<Page<Card>> fetchPaginatedCardList(SearchDto searchDto, Pageable pageable) {
        return cardService.fetchPaginatedCardList(searchDto, pageable);
    }
}
