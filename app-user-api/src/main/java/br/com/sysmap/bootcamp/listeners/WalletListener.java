package br.com.sysmap.bootcamp.listeners;

import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RabbitListener(queues = "walletQueue")
public class WalletListener {

    @Autowired
    private WalletService walletService;

    @RabbitHandler
    public void receive(WalletDto walletDto) {

        walletService.debit(walletDto);

        log.info("Debiting wallet: {}", walletDto);

    }
}
