package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.InvalidCredentialsException;
import br.com.sysmap.bootcamp.domain.exceptions.UnauthorizedAccessException;
import br.com.sysmap.bootcamp.domain.exceptions.UserAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final WalletCreationService walletCreationService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    @Transactional
    public Users save(Users users) {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(users.getEmail());

        if (usersOptional.isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        users = users.toBuilder().password(this.passwordEncoder.encode(users.getPassword())).build();

        log.info("Saving user: {}", users);
        Users savedUsers = this.usersRepository.save(users);

        this.walletCreationService.createUserWallet(savedUsers);

        return savedUsers;
    }

    @Transactional
    public Users update(Users updatedUsers) {
        Users oldUsers = findByEmail(securityUtils.getAuthenticatedUserEmail());

        if (!Objects.equals(updatedUsers.getId(), oldUsers.getId())) {
            throw new UnauthorizedAccessException("Attempting to modify a user other than the authenticated user");
        }

        if (!Objects.equals(oldUsers.getEmail(), updatedUsers.getEmail())) {
            Optional<Users> usersOptional = this.usersRepository.findByEmail(updatedUsers.getEmail());

            if (usersOptional.isPresent()) {
                throw new UserAlreadyExistsException("Email already exists");
            }
        }

        log.info("Updating user: {}", oldUsers);
        return usersRepository.save(
                oldUsers.toBuilder()
                        .name(updatedUsers.getName())
                        .email(updatedUsers.getEmail())
                        .password(this.passwordEncoder.encode(updatedUsers.getPassword()))
                        .build());
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(username);

        return usersOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }

    public Users findByEmail(String email) {
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Users findById(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }


    public AuthDto auth(AuthDto authDto) {
        Users users = this.findByEmail(authDto.getEmail());

        if (!this.passwordEncoder.matches(authDto.getPassword(), users.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        String password = users.getEmail() + ":" + users.getPassword();

        String encodedPassword = Base64.getEncoder()
                .withoutPadding()
                .encodeToString(password.getBytes());

        return AuthDto.builder()
                .email(users.getEmail())
                .token(encodedPassword)
                .id(users.getId())
                .build();
    }

}
