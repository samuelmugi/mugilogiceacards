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
public class SignUpRequest {

  @NotNull(message = "First name cannot be null")
  private String firstName;

  @NotNull(message = "Last Name cannot be null")
  private String lastName;


  @NotNull(message = "Login cannot be null")
  @Email(message = "Email should be valid")
  private String email;


  @NotNull(message = "password cannot be null")
  private String password;
  @NotNull(message = "password cannot be null")
  private String confirmPassword;

  @NotNull(message = "Role cannot be null")
  private String role;

}
