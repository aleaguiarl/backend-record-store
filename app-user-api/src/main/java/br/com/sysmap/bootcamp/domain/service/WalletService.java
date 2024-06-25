package br.com.sysmap.bootcamp.domain.service;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import br.com.sysmap.bootcamp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final UsersService usersService;
    private final WalletRepository walletRepository;
    private final SecurityUtils securityUtils;

    public Wallet getWallet() {
        Users authenticatedUsers = usersService.findByEmail(securityUtils.getAuthenticatedUserEmail());

        Optional<Wallet> walletOptional = walletRepository.findByUsers(authenticatedUsers);
        if (walletOptional.isEmpty()) {
            throw new RuntimeException("User doesn't have a wallet");
        }

        return walletOptional.get();
    }


    @Transactional
    public void debit(WalletDto walletDto) {
        Users users = usersService.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository
                .findByUsers(users)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal newBalance = wallet.getBalance().subtract(walletDto.getValue());
        wallet.setBalance(newBalance);

        wallet.setLastUpdate(LocalDateTime.now());

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        creditPointsToWallet(wallet, dayOfWeek);

        walletRepository.save(wallet);
    }

    @Transactional
    public Wallet credit(BigDecimal value) {
        Users authenticadeUsers = getUser();

        Wallet wallet = walletRepository
                .findByUsers(authenticadeUsers)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(value));
        wallet.setLastUpdate(LocalDateTime.now());

        return walletRepository.save(wallet);
    }

    private void creditPointsToWallet(Wallet wallet, DayOfWeek dayOfWeek) {
        Long pointsToAdd;

        switch (dayOfWeek) {

            case MONDAY -> pointsToAdd = 7L;
            case TUESDAY -> pointsToAdd = 6L;
            case WEDNESDAY -> pointsToAdd = 2L;
            case THURSDAY -> pointsToAdd = 10L;
            case FRIDAY -> pointsToAdd = 15L;
            case SATURDAY -> pointsToAdd = 20L;
            case SUNDAY -> pointsToAdd = 25L;
            default -> throw new RuntimeException("Invalid day of the week.");
        }

        wallet.setPoints(wallet.getPoints() + pointsToAdd);
    }

    private Users getUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal().toString();

        return usersService.findByEmail(email);
    }

}
