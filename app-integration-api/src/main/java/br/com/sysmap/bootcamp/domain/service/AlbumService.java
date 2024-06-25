package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.AlbumAlreadyBoughtException;
import br.com.sysmap.bootcamp.domain.exceptions.InvalidAlbumIdException;
import br.com.sysmap.bootcamp.domain.exceptions.UnauthorizedAccessException;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {

    private final Queue queue;
    private final RabbitTemplate template;
    private final SpotifyApi spotifyApi;
    private final AlbumRepository albumRepository;
    private final UsersService usersService;

    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        return this.spotifyApi.getAlbums(search);
    }

    public List<Album> getUserAlbums() {
        return albumRepository.findAllByUsers(getUser());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Album saveAlbum(Album album) {
        List<Album> userAlbumList = albumRepository.findAllByUsers(getUser());
        for (Album alb: userAlbumList) {
            if (alb.getIdSpotify().equals(album.getIdSpotify())) {
                throw new AlbumAlreadyBoughtException("The user already has bought the album.");
            }
        }

        album.setUsers(getUser());
        Album albumSaved = albumRepository.save(album);

        WalletDto walletDto = new WalletDto(album.getUsers().getEmail(), album.getValue());
        this.template.convertAndSend(queue.getName(), walletDto);

        return albumSaved;
    }

    public void deleteAlbum(Long albumId) {
        Optional<Album> optionalAlbum = albumRepository.findById(albumId);

        if (optionalAlbum.isEmpty()) {
            throw new InvalidAlbumIdException("Invalid album ID.");
        }

        if (!Objects.equals(getUser().getId(), albumRepository.findUsersIdById(albumId))) {
            throw new UnauthorizedAccessException("Error trying to delete an album from other user collection");
        }

        albumRepository.deleteById(albumId);
    }

    private Users getUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal().toString();

        return usersService.findByEmail(email);
    }


}
