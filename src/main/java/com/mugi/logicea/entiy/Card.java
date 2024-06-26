package com.mugi.logicea.entiy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mugi.logicea.cards.dtos.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String color;
    private String description;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    private Date createDate;
    private String createBy;
    @JsonIgnore
    private Date modifiedDate;
    @JsonIgnore
    private String modifiedBy;

}
