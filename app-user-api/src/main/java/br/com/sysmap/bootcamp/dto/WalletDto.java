package br.com.sysmap.bootcamp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class WalletDto implements Serializable {

    private String email;
    private BigDecimal value;

}
