package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.InvalidCredentialsException;
import br.com.sysmap.bootcamp.domain.exceptions.UserAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private WalletCreationService walletCreationService;

    @MockBean
    private WalletRepository walletRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() {
        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .password("test")
                .build();

        when(usersRepository.save(any(Users.class))).thenReturn(users);

        Users savedUsers = usersService.save(users);

        assertEquals(savedUsers, users);
    }

    @Test
    @DisplayName("Should return users when a valid id is given")
    public void shouldReturnUsersWhenValidIdGiven() {
        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .password("test")
                .build();

        when(usersRepository.findById(users.getId())).thenReturn(Optional.of(users));

        Users foundUsers = usersService.findById(users.getId());

        assertEquals(users, foundUsers);
    }

    @Test
    @DisplayName("Should throw a RuntimeException on duplicate email during user creation.")
    public void shouldThrowExceptionWhenUserWithDuplicateEmailIsSaved() throws Exception {
        Users users = Users.builder()
                .id(1L).name("test")
                .email("test@email.com")
                .password("test")
                .build();

        when(usersRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));

        assertThrows(UserAlreadyExistsException.class, () -> usersService.save(users));
    }

    @Test
    @DisplayName("Should updated user successfully when update is valid")
    public void shouldUpdatedUsersSuccessfullyWhenValidUsersIsUpdated() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Users oldUsers = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .password("test")
                .build();

        Users newUsers = Users.builder()
                .id(1L)
                .name("test update")
                .email("test_update@email.com")
                .password("test_update_password")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(oldUsers.getEmail());
        when(usersRepository.findByEmail(oldUsers.getEmail())).thenReturn(Optional.of(oldUsers));
        when(usersRepository.save(any(Users.class))).thenReturn(newUsers);

        Users savedNewUsers = usersService.update(newUsers);

        assertEquals(savedNewUsers, newUsers);

        verify(usersRepository, times(1)).save(newUsers);
    }

    @Test
    @DisplayName("Should not encode the given password when it's already encoded")
    public void shouldNotEncodeGivenPasswordWhenAlreadyEncoded() {
        Users newUsers = Users.builder()
                .id(1L)
                .name("test update")
                .email("test_update@email.com")
                .password("test_update_password")
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(newUsers.getEmail());
        when(usersRepository.findByEmail(newUsers.getEmail())).thenReturn(Optional.of(newUsers));
    }

    @Test
    @DisplayName("Should return a list with all users")
    public void shouldReturnAllUsersLit() {
        Users users1 = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .password("test")
                .build();

        Users users2 = Users.builder()
                .id(2L)
                .name("test2")
                .email("test2@email.com")
                .password("test2")
                .build();

        List<Users> usersList = new ArrayList<>();
        usersList.add(users1);
        usersList.add(users2);

        when(usersRepository.findAll()).thenReturn(usersList);

        List<Users> resultList = usersService.getAllUsers();

        assertEquals(usersList, resultList);
    }

    @Test
    @DisplayName("Should authenticate a user successfully when user credentials is valid")
    public void shouldAuthenticateUserSuccessfullyWhenUserCredentialsIsValid() {
        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .password("test")
                .build();

        AuthDto authDto = AuthDto.builder()
                .id(1L)
                .email("test@email.com")
                .password("test")
                .build();

        when(usersRepository.findByEmail(authDto.getEmail())).thenReturn(Optional.of(users));
        when(passwordEncoder.matches(authDto.getPassword(), users.getPassword())).thenReturn(true);

        AuthDto savedAuthDto = usersService.auth(authDto);

        assertEquals(savedAuthDto.getId(), authDto.getId());
        assertEquals(savedAuthDto.getEmail(), authDto.getEmail());
        assertNull(savedAuthDto.getPassword());
        assertNotNull(savedAuthDto.getToken());

    }

    @Test
    @DisplayName("Should throw a RuntimeException when the passwords don't match")
    public void shouldThrowRuntimeExceptionWhenPasswordsDontMatch() {
        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .password("test")
                .build();

        AuthDto authDto = AuthDto.builder()
                .id(1L)
                .email("test@email.com")
                .password("test123")
                .build();

        when(usersRepository.findByEmail(authDto.getEmail())).thenReturn(Optional.of(users));
        when(passwordEncoder.matches(authDto.getPassword(), users.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> usersService.auth(authDto));

    }

}
