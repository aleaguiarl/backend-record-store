package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "User API")
public class UsersController {

    private final UsersService usersService;

    @Operation(summary = "Save user", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "409", description = "Email already exists")})
    @PostMapping("/create")
    public ResponseEntity<Users> save(@RequestBody @Valid Users users) {
        return ResponseEntity.ok(this.usersService.save(users));
    }

    @Operation(summary = "Authorize user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Invalid credentials")})
    @PostMapping("/auth")
    public ResponseEntity<AuthDto> auth(@RequestBody AuthDto authDto) {
        return ResponseEntity.ok(this.usersService.auth(authDto));
    }

    @Operation(summary = "Retrieve user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Users> retrieveUserById(@PathVariable Long id) {
        return ResponseEntity.ok(usersService.findById(id));
    }

    @Operation(summary = "Retrieve users list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @Operation(summary = "Update an user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Error attempting to modify a user other than the authenticated user"),
            @ApiResponse(responseCode = "409", description = "Email already exists")})
    @PutMapping("/update")
    public ResponseEntity<Users> updateUser(@RequestBody @Valid Users users) {
        return ResponseEntity.ok(usersService.update(users));
    }

}


