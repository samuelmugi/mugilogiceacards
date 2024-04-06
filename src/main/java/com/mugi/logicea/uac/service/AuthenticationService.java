package com.mugi.logicea.uac.service;


import com.mugi.logicea.entiy.User;
import com.mugi.logicea.repository.UserRepository;
import com.mugi.logicea.security.JwtService;
import com.mugi.logicea.uac.dtos.*;
import com.mugi.logicea.uac.mapper.UACMapper;
import com.mugi.logicea.utils.ErrorMessage;
import com.mugi.logicea.utils.RestResponse;
import com.mugi.logicea.utils.RestResponseObject;
import com.mugi.logicea.utils.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.*;

import static com.mugi.logicea.utils.UtilFunctions.getCurrentUser;
import static com.mugi.logicea.utils.UtilFunctions.validateNotNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @EventListener(ApplicationReadyEvent.class)
    private void loadOutletTypes() {
        StopWatch watch = new StopWatch();
        watch.start();
        SignUpRequest normalUser = SignUpRequest.builder()
                .email("user@logicea.mugi")
                .firstName("user")
                .lastName("user")
                .password("user@1234")
                .confirmPassword("user@1234")
                .role(String.valueOf(Role.USER))
                .build();
        createUser(normalUser);
        log.info("Logicea Normal user  created with email:{} and  password:{}",normalUser.getEmail(),normalUser.getPassword());
        SignUpRequest adminUser = SignUpRequest.builder()
                .email("admin@logicea.mugi")
                .firstName("admin")
                .lastName("admin")
                .password("admin@1234")
                .confirmPassword("admin@1234")
                .role(String.valueOf(Role.ADMIN))
                .build();
        createUser(adminUser);
        log.info("Logicea Admin user created with email:{} and password:{}",adminUser.getEmail(),adminUser.getPassword());

        log.info("Init App users:{}", Instant.now());
        watch.stop();
        log.info("Init App users Ended at :{} after :{}", Instant.now(), watch.getTotalTimeSeconds());
    }


    public RestResponse createUser(SignUpRequest request) {
        User user = UACMapper.INSTANCE.signUpToUserMapper(request);

        List<ErrorMessage> validateObj = validateNotNull(request);
        if (ObjectUtils.isNotEmpty(validateObj)) {

            return new RestResponse(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }

        List<ErrorMessage> errorMessages = validateUserDetails(request);
        if (ObjectUtils.isNotEmpty(errorMessages)) {

            return new RestResponse(
                    RestResponseObject.builder()
                            .message("Invalid User Details!!")
                            .errors(errorMessages)
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            var currentUser = getCurrentUser();
            User toSave = user.toBuilder()
                    .createDate(Date.from(Instant.now()))
                    .modifiedDate(Date.from(Instant.now()))
                    .createBy(currentUser)
                    .modifiedBy(currentUser)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .active(true)
                    .build();


            userRepository.save(toSave);
            return new RestResponse(
                    RestResponseObject.builder().message("User Created Successfully").build(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("signup:- " + ex.getMessage());
            return new RestResponse(
                    RestResponseObject.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }


    public RestResponse updateUser(SignUpUpdateRequest request) {
        User user = UACMapper.INSTANCE.signUpDateToUserMapper(request);

        List<ErrorMessage> validateObj = validateNotNull(request);
        if (ObjectUtils.isNotEmpty(validateObj)) {

            return new RestResponse(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<User> existingUser = userRepository.findById(request.getUserId());
        if (existingUser.isEmpty())
            return new RestResponse(
                    RestResponseObject.builder()
                            .message("User ID does not exist")
                            .errors(
                                    Collections.singletonList(
                                            ErrorMessage.builder()
                                                    .field("userId")
                                                    .message("User ID does not exist")
                                                    .build()))
                            .build(),
                    HttpStatus.BAD_REQUEST);

        try {
            var currentUser = getCurrentUser();

            User toSave =
                    user.toBuilder()
                            .createDate(existingUser.get().getCreateDate())
                            .createBy(existingUser.get().getCreateBy())
                            .modifiedDate(Date.from(Instant.now()))
                            .modifiedBy(currentUser)
                            .password(existingUser.get().getPassword())
                            .active(true)
                            .build();


            userRepository.save(toSave);
            return new RestResponse(
                    RestResponseObject.builder().message("User Created Successfully").build(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("signup:- " + ex.getMessage());
            return new RestResponse(
                    RestResponseObject.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    private List<ErrorMessage> validateUserDetails(SignUpRequest request) {
        List<ErrorMessage> errorMessageList = new ArrayList<>();

        var reqRole= request.getRole();
        if(!List.of(Role.ADMIN.name(), Role.USER.name()).contains(reqRole)){
            log.error("Invalid role :{} supplied!",reqRole);
            errorMessageList.add(
                    ErrorMessage.builder().field("role").message("Invalid role :"+reqRole+" supplied!").build());
        }

        Optional<User> existingEmail = userRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()){
            log.error("Duplicate email:{} found!",request.getEmail());
            errorMessageList.add(
                    ErrorMessage.builder().field("email").message("Duplicate email found!").build());}

        return errorMessageList;
    }

    public JwtAuthenticationResponse signIn(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        var user =
                userRepository
                        .findByEmail(request.getLogin())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


    public RestResponse fetchUser(SearchDto searchDto) {
        List<ErrorMessage> validateObj = validateNotNull(searchDto);
        if (ObjectUtils.isNotEmpty(validateObj)) {
            return new RestResponse(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        try {
//      List<User> configs =
//              userCustomAppRepository.findByFieldAndValue(
//              User.class, searchDto.getFieldName(), searchDto.getValue());
//      List<UserResponse> response = BusinessMapper.INSTANCE.userEntotyToUserResponseList(configs);
            return new RestResponse(
                    RestResponseObject.builder().message("Fetched Successfully")
//                  .payload(response)
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("fetchBusinessEntity Exception:-" + e.getMessage());
            return new RestResponse(
                    RestResponseObject.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }


    public RestResponse fetchPaginatedUserList(SearchDto searchDto, Pageable pageable) {
        try {
//      Query query = new Query();
//      if (ObjectUtils.isEmpty(searchDto.getFieldName())
//          && ObjectUtils.isNotEmpty(searchDto.getValue()))
//        query.addCriteria(Criteria.where(searchDto.getFieldName()).regex(searchDto.getValue()));
//      Page<User> page =
//              userCustomAppRepository.findByQueryWithPagination(
//              User.class, query, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

            return new RestResponse(
                    RestResponseObject.builder().message("Fetched Successfully")
//                  .payload(page)
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("fetchPaginatedBusinessEntityList Exception:-" + e.getMessage());
            return new RestResponse(
                    RestResponseObject.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }


    public RestResponse changePassowrd(ChangePasswordRequest changePasswordRequest) {
        List<ErrorMessage> validateObj = validateNotNull(changePasswordRequest);
        if (ObjectUtils.isNotEmpty(validateObj)) {

            return new RestResponse(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByEmail(changePasswordRequest.getLogin());
        if (user.isEmpty()) {
            return new RestResponse(
                    RestResponseObject.builder().message("User does not exist!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        User existingUser =
                user.get().toBuilder()
                        .password(passwordEncoder.encode(changePasswordRequest.getPassword()))
                        .build();
        userRepository.save(existingUser);
        return new RestResponse(
                RestResponseObject.builder().message("Updated password successfully").build(),
                HttpStatus.OK);
    }


    public RestResponse changeUserStatus(ChangeStatusRequest changeStatusRequest) {
        List<ErrorMessage> validateObj = validateNotNull(changeStatusRequest);
        if (ObjectUtils.isNotEmpty(validateObj)) {

            return new RestResponse(
                    RestResponseObject.builder().message("Invalid Fields!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByEmail(changeStatusRequest.getLogin());
        if (user.isEmpty()) {
            return new RestResponse(
                    RestResponseObject.builder().message("User does not exist!!").errors(validateObj).build(),
                    HttpStatus.BAD_REQUEST);
        }
        User existingUser = user.get().toBuilder().active(changeStatusRequest.isStatus()).build();
        userRepository.save(existingUser);
        return new RestResponse(
                RestResponseObject.builder().message("Changed Status Successfully").build(), HttpStatus.OK);
    }



}