package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @MockBean
    private UsersService usersService;

    @MockBean
    private WalletRepository walletRepository;

    @Test
    @DisplayName("Should return a valid wallet when given a valid user")
    public void shouldReturnValidWalletWhenGivenAValidUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .users(users)
                .build();


        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(users.getEmail());
        when(usersService.findByEmail(users.getEmail())).thenReturn(users);
        //when(usersService.findById(users.getId())).thenReturn(users);
        when(walletRepository.findByUsers(users)).thenReturn(Optional.of(wallet));

        Wallet retrievedWallet = walletService.getWallet();

        assertEquals(wallet, retrievedWallet);

    }

    @Test
    @DisplayName("Should deduct from balance successfully when given a valid wallet")
    public void shouldDebitSuccessfullyFromWalletGivenValidWallet() {
        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .lastUpdate(LocalDateTime.now())
                .points(200L)
                .users(users)
                .build();

        WalletDto walletDto = new WalletDto(users.getEmail(), BigDecimal.valueOf(200));

        when(usersService.findByEmail(walletDto.getEmail())).thenReturn(users);
        when(walletRepository.findByUsers(users)).thenReturn(Optional.of(wallet));

        walletService.debit(walletDto);

        verify(walletRepository, times(1)).save(wallet);

        assertEquals(BigDecimal.valueOf(800), wallet.getBalance());
    }

    @Test
    @DisplayName("Should throw a RuntimeException when debiting a wallet that is not found")
    public void shouldThrowRuntimeExceptionWhenDebitingWalletIsNotFound() {
        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .lastUpdate(LocalDateTime.now())
                .points(200L)
                .users(users)
                .build();

        WalletDto walletDto = new WalletDto(users.getEmail(), BigDecimal.valueOf(200));

        when(usersService.findByEmail(walletDto.getEmail())).thenReturn(users);
        when(walletRepository.findByUsers(users)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> walletService.debit(walletDto));
    }

    @Test
    @DisplayName("Should add credit successfully when wallet is found and user is authenticated correctly")
    public void shouldAddCreditSuccessfullyWhenWalletFoundAndUserAuthenticatedCorrectly() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .points(200L)
                .users(users)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(users.getEmail());
        when(usersService.findByEmail(users.getEmail())).thenReturn(users);
        when(walletRepository.findByUsers(users)).thenReturn(Optional.of(wallet));

        BigDecimal expectedBalance = wallet.getBalance().add(BigDecimal.valueOf(200));

        walletService.credit(BigDecimal.valueOf(200));

        assertEquals(expectedBalance, wallet.getBalance());
    }

    @Test
    @DisplayName("Should throw a RuntimeException when crediting a wallet that is not found")
    public void shouldThrowRuntimeExceptionWhenCreditingWalletIsNotFound() {
        Users users = Users.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .users(users)
                .build();

        WalletDto walletDto = new WalletDto(users.getEmail(), BigDecimal.valueOf(200));

        when(usersService.findByEmail(walletDto.getEmail())).thenReturn(users);
        when(walletRepository.findByUsers(users)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> walletService.credit(BigDecimal.TEN));
    }
}
