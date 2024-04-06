package com.mugi.logicea.uac.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
     private String email;
     private Role role;
    private Date createDate;
    private String createBy;
    private Date modifiedDate;
    private String modifiedBy;
    private boolean active;
 }
