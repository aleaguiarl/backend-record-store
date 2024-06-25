package br.com.sysmap.bootcamp.domain.mapper;


import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "balance", target = "value")
    WalletDto walletToWalletDto(Wallet wallet);

}
