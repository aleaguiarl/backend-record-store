package br.com.sysmap.bootcamp.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {

    private String name;
    private String email;

}
