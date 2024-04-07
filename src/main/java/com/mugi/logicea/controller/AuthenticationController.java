package com.mugi.logicea.controller;


import com.mugi.logicea.entiy.User;
import com.mugi.logicea.uac.dtos.*;
import com.mugi.logicea.uac.service.AuthenticationService;
import com.mugi.logicea.utils.RestResponseObject;
import com.mugi.logicea.utils.SearchDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/uac")
@RequiredArgsConstructor
@CrossOrigin (origins = "*")
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/createUser")
  public ResponseEntity<RestResponseObject> createUser(@RequestBody SignUpRequest request) {
    return authenticationService.createUser(request);
  }  @PostMapping("/updateUser")
  public ResponseEntity<RestResponseObject> updateUser(@RequestBody SignUpUpdateRequest request) {
    return authenticationService.updateUser(request);
  }

  @PostMapping("/signin")
  public ResponseEntity<JwtAuthenticationResponse> signin(
      @Valid @RequestBody SigninRequest request) {
    return ResponseEntity.ok(authenticationService.signIn(request));
  }

  @PostMapping("/changePassword")
  public ResponseEntity<RestResponseObject> changePassowrd(@Valid @RequestBody ChangePasswordRequest request) {
    return authenticationService.changePassowrd(request);
  }

  @PostMapping("/changeStatus")
  public ResponseEntity<RestResponseObject> changeUserStatus(
      @Valid @RequestBody ChangeStatusRequest changeStatusRequest) {
    return authenticationService.changeUserStatus(changeStatusRequest);
  }



  @GetMapping(path = "/search", produces = "application/json")
  ResponseEntity<User> fetchUser(SearchDto searchDto) {
    return authenticationService.fetchUser(searchDto);
  }

  @GetMapping(path = "/list", produces = "application/json")
  ResponseEntity<Page<User>> fetchPaginatedUserList(SearchDto searchDto, Pageable pageable) {
    return authenticationService.fetchPaginatedUserList(searchDto, pageable);
  }
}
