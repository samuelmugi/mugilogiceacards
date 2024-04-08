package com.mugi.logicea.uac.service;

import com.mugi.logicea.entiy.User;
import com.mugi.logicea.repository.UserRepository;
import com.mugi.logicea.security.JwtService;
import com.mugi.logicea.uac.dtos.*;
import com.mugi.logicea.utils.RestResponseObject;
import com.mugi.logicea.utils.SearchDto;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mugi.logicea.uac.dtos.Role.ROLE_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * created on 08/04/2024
 *
 * @author Mugi
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;


    @InjectMocks
    AuthenticationService authenticationService;

    private User user;
    @BeforeEach
    void setUp() {
          user = User.builder()
                .id(1L)
                .email("user@logicea.mugi")
                .firstName("user")
                .lastName("user")
                .password(passwordEncoder.encode("user@1234"))
                .role(String.valueOf(Role.ROLE_MEMBER))
                .active(true)
                .createDate(new Date())
                .modifiedDate(new Date())
                .createBy("test")
                .modifiedBy("test")
                .build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

    }
    @Test
    void createUser() {
        // Arrange
        SignUpRequest request = SignUpRequest.builder()
                .email("user@logicea.mugi")
                .firstName("user")
                .lastName("user")
                .password("user@1234")
                .confirmPassword("user@1234")
                .role(String.valueOf(ROLE_MEMBER))
                .build();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<RestResponseObject<User>> response = authenticationService.createUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Created Successfully", response.getBody().getMessage());
        assertEquals("user@logicea.mugi", response.getBody().getPayload().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser() {
        // Arrange
        Long userId = 1L;
        SignUpUpdateRequest request = new SignUpUpdateRequest();
        request.setUserId(String.valueOf(userId));
        request.setEmail("user@logicea.mugi");
        request.setFirstName("user");
        request.setLastName("user");
        request.setRole(String.valueOf(ROLE_MEMBER));

        User user = new User();
        user.setId(userId);
        user.setEmail("user@logicea.mugi");
        user.setFirstName("user");
        user.setLastName("user");
        user.setPassword(passwordEncoder.encode("user@1234"));
        user.setRole(String.valueOf(ROLE_MEMBER));
        user.setActive(true);
        user.setCreateDate(new Date());
        user.setCreateBy("test");
        user.setModifiedDate(new Date());
        user.setModifiedBy("test");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        // Act
        ResponseEntity<RestResponseObject<User>> response = authenticationService.updateUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Updated Successfully", response.getBody().getMessage());
        assertEquals(user, response.getBody().getPayload());
    }

    @Test
    void signIn() {
        // Arrange
        Long userId = 1L;
        String email = "user@logicea.mugi";
        String password = "user@1234";
        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        user.setFirstName("user");
        user.setLastName("user");
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(String.valueOf(ROLE_MEMBER));
        user.setActive(true);
        user.setCreateDate(new Date());
        user.setCreateBy("test");
        user.setModifiedDate(new Date());
        user.setModifiedBy("test");
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        when(jwtService.generateToken(any())).thenReturn(String.valueOf(UUID.randomUUID()));

        // Act
        JwtAuthenticationResponse response = authenticationService.signIn(
                SigninRequest.builder().login(email).password(password).build());

        // Assert
        assertNotNull(response);
         assertNotNull(response.getToken());
    }

    @Test
    void fetchUser() {
        // Arrange
        SearchDto searchDto = new SearchDto();
        searchDto.setFieldName("email");
        searchDto.setValue(user.getEmail());

        // Act
        when(userRepository.findOne((Specification<User>) any())).thenReturn(Optional.of(user));

        ResponseEntity<RestResponseObject<User>> response = authenticationService.fetchUser(searchDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getPayload());
        assertEquals("Fetched Successfully", response.getBody().getMessage());
        User user = response.getBody().getPayload();
        assertEquals(user.getEmail(), user.getEmail());
    }

    @Test
    void fetchPaginatedUserList() {
        // given
        SearchDto searchDto = new SearchDto("email", "test@test.com");
        Pageable pageable = PageRequest.of(0, 10);
        given(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(user), pageable, 1));

        // when
        ResponseEntity<RestResponseObject<Page<User>>> responseEntity =
                authenticationService.fetchPaginatedUserList(searchDto, pageable);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
         assertThat(responseEntity.getBody().getPayload()).isNotEmpty();
        assertThat(responseEntity.getBody().getPayload().getTotalElements()).isEqualTo(1);
        assertThat(responseEntity.getBody().getPayload().getSize()).isEqualTo(10);
        assertThat(responseEntity.getBody().getPayload().getTotalElements()).isEqualTo(1);
        verify(userRepository, times(1))
                .findAll(any(Specification.class), any(Pageable.class));

    }

    @Test
    void changePassowrd() {

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setLogin("user@logicea.mugi");
        changePasswordRequest.setPassword("<PASSWORD>");
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        ResponseEntity<RestResponseObject> response = authenticationService.changePassowrd(changePasswordRequest);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated password successfully",response.getBody().getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changeUserStatus() {
        // Arrange
        String email = "user@example.com";
        boolean status = false;
        ChangeStatusRequest changeStatusRequest = new ChangeStatusRequest(email, status);
        User statusUser = user;
        statusUser.setActive(status);
        when(userRepository.save(any())).thenReturn(statusUser);

        // Act
        ResponseEntity<RestResponseObject<User>> response = authenticationService.changeUserStatus(changeStatusRequest);


        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Changed Status Successfully", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
        assertNotNull(response.getBody().getPayload());
    }
}