package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet Controller", description = "User API")
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Get authenticated user wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")}
    )
    @GetMapping
    public ResponseEntity<Wallet> getWallet() {
        return ResponseEntity.ok(walletService.getWallet());
    }

    @Operation(summary = "Credit authenticated user wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")}
    )
    @PutMapping("/credit/{value}")
    public ResponseEntity<Wallet> credit(@PathVariable BigDecimal value) {
        return ResponseEntity.ok(walletService.credit(value));
    }

}
