package com.mugi.logicea.uac.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUpdateRequest {

  @NotNull(message = "userId cannot be null")
  private String userId;

  @NotNull(message = "First name cannot be null")
  private String firstName;

  @NotNull(message = "Last Name cannot be null")
  private String lastName;


  @Email(message = "Email should be valid")
  private String email;


  @NotNull(message = "Role cannot be null")
  private String role;

}
