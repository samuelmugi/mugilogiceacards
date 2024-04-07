package com.mugi.logicea.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class UtilFunctions {
  private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();




  public static <T> List<ErrorMessage> validateNotNull(Object obj) {

    List<ErrorMessage> errors = new ArrayList<>();

    Set<ConstraintViolation<Object>> violations = validator.validate(obj);
    for (ConstraintViolation<Object> violation : violations) {
      errors.add(
          ErrorMessage.builder()
              .field(violation.getPropertyPath().toString())
              .message(violation.getMessage())
              .build());

      log.error(violation.getPropertyPath().toString() + "-" + violation.getMessage());
    }

    return errors; // All fields are not null, validation passes
  }

  public static String getCurrentUser() {
    // Get the authentication object from the SecurityContextHolder
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Check if the authentication object is not null and if the user is authenticated
    if (authentication != null && authentication.isAuthenticated()) {
      // Get the username from the authentication object
        return authentication.getName();
    } else {
      return "User not authenticated";
    }
  }


}
