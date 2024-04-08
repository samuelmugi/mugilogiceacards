package com.mugi.logicea.cards.service;

import com.mugi.logicea.cards.dtos.CreateCardRequest;
import com.mugi.logicea.cards.dtos.UpdateCardRequest;
import com.mugi.logicea.entiy.Card;
import com.mugi.logicea.entiy.User;
import com.mugi.logicea.repository.CardRepository;
import com.mugi.logicea.uac.dtos.Role;
import com.mugi.logicea.utils.RestResponseObject;
import com.mugi.logicea.utils.SearchDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mugi.logicea.cards.dtos.CardStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * created on 08/04/2024
 *
 * @author Mugi
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    CardService cardService;

    Card card;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeAll
    static void setUpAll() {
        var role = Role.ROLE_MEMBER.toString();
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@logicea.mugi");
        User userDetails = new User();
        userDetails.setEmail("user@logicea.mugi");
        userDetails.setRole(role);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(authority);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

    }

    @BeforeEach
    void setUp() {
        card = Card.builder()
                .id(1L)
                .name("Test Card")
                .color(String.valueOf(Color.BLACK))
                .description("This is a test card")
                .createBy("user@logicea.mugi")
                .createDate(new Date())
                .build();
        when(cardRepository.save(any())).thenReturn(card);
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(cardRepository.findOne((Specification<Card>) any())).thenReturn(Optional.of(card));
        when(cardRepository.findAll((Specification<Card>) any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(card), pageable, 1));

    }

    @Test
    void createCard() {
        // given
        CreateCardRequest request = CreateCardRequest.builder()
                .name("Test Card")
                .description("This is a test card")
                .build();

        // when
        ResponseEntity<RestResponseObject<Card>> response = cardService.createCard(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Card Created Successfully");
        assertThat(response.getBody().getPayload()).isNotNull();
    }

    @Test
    void updateCard() {
        // given
        Long id = 1L;
        UpdateCardRequest request = UpdateCardRequest.builder()
                .name("Updated Test Card")
                .description("This is an updated test card")
                .status(String.valueOf(IN_PROGRESS))
                .build();

        // when
        ResponseEntity<RestResponseObject<Card>> response = cardService.updateCard(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Card Updated Successfully");
        assertThat(response.getBody().getPayload()).isNotNull();
    }

    @Test
    void fetchCard() {
        // given
        SearchDto searchDto = SearchDto.builder()
                .fieldName("name")
                .value("Test Card")
                .build();

        // when
        ResponseEntity<RestResponseObject<Card>> response = cardService.fetchCard(searchDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Fetched Successfully");
        assertThat(response.getBody().getPayload()).isNotNull();
    }

    @Test
    void fetchPaginatedCardList() {
        // given
        SearchDto searchDto = SearchDto.builder()
                .fieldName("title")
                .value("Test Card")
                .build();
        Pageable pageable = Pageable.unpaged();

        // when
        ResponseEntity<RestResponseObject<Page<Card>>> response = cardService.fetchPaginatedCardList(searchDto, pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPayload()).isNotEmpty();
    }

    @Test
    void deleteCard() {
        // given
        Long id = 1L;

        // when
        ResponseEntity<RestResponseObject> response = cardService.deleteCard(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Card Deleted Successfully");
    }
}