package br.com.sysmap.bootcamp.domain.mapper;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.dto.UsersDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsersMapper {

    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);
    UsersDto usersToUsersDto(Users users);

}