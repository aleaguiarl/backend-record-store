package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalletCreationService {

    private  final WalletRepository walletRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void createUserWallet(Users users) {
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(2000))
                .users(users)
                .points(0L)
                .lastUpdate(LocalDateTime.now()).build();

        log.info("Creating user wallet: {}", wallet);
        walletRepository.save(wallet);
    }
}
