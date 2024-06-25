package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "Get spotify albums by search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/all")
    public ResponseEntity<List<AlbumModel>> getAlbumsFromSpotify(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(search));
    }

    @Operation(summary = "Get user albums list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
    })
    @GetMapping("/my-collection")
    public ResponseEntity<List<Album>> getUserAlbums() {
        return ResponseEntity.ok(albumService.getUserAlbums());
    }

    @Operation(summary = "Save an album in a user collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "409", description = "The user already has bought the album")})
    @PostMapping("/sale")
    public ResponseEntity<Album> saveAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(this.albumService.saveAlbum(album));
    }

    @Operation(summary = "Delete an album from a user collection by id parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Error trying to delete an album from other user collection"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }


}
