package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AlbumServiceTest {

    @Autowired
    private AlbumService albumService;

    @MockBean
    private Queue queue;

    @MockBean
    private RabbitTemplate template;

    @MockBean
    private SpotifyApi spotifyApi;

    @MockBean
    private AlbumRepository albumRepository;

    @MockBean
    private UsersService usersService;

    @Test
    @DisplayName("Should return album when a valid album is saved")
    public void shouldReturnAlbumWhenValidAlbumIsSaved() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Users users = Users.builder()
                .id(1L)
                .email("test@email.com")
                .password("test")
                .build();

        Album album = Album.builder()
                .idSpotify("1234567")
                .value(BigDecimal.valueOf(24.50))
                .users(users)
                .build();

        when(albumRepository.findAllByUsers(users)).thenReturn(new ArrayList<>());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(users.getEmail());
        when(usersService.findByEmail(users.getEmail())).thenReturn(users);

        when(albumRepository.save(album)).thenReturn(album);

        Album savedAlbum = albumService.saveAlbum(album);

        assertNotNull(savedAlbum);
        assertEquals(album, savedAlbum);

        verify(albumRepository, times(1)).findAllByUsers(users);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    @DisplayName("Should throw AlbumAlreadyBoughtException when saving a album the user already bought")
    public void shouldThrowAlbumAlreadyBoughtExceptionWhenUserAlreadyBoughtTheAlbum() {
        Users users = Users.builder()
                .id(1L)
                .email("test@email.com")
                .password("test")
                .build();

        Album album = Album.builder()
                .idSpotify("1234567")
                .value(BigDecimal.valueOf(13.26))
                .users(users)
                .build();

        List<Album> usersAlbumList = new ArrayList<>();
        usersAlbumList.add(album);

        when(albumRepository.findAllByUsers(users)).thenReturn(usersAlbumList);

        assertThrows(RuntimeException.class, () -> albumService.saveAlbum(album));

        verify(albumRepository, never()).save(album);
    }

    @Test
    @DisplayName("Should delete album successfully when album ID exists")
    public void shouldDeleteAlbumSuccessfullyWhenAlbumIdExists() {
        Long albumId = 1L;

        Album album = Album.builder()
                .id(albumId)
                .build();

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.deleteAlbum(albumId);

        verify(albumRepository, times(1)).deleteById(albumId);
    }

    @Test
    @DisplayName("Should throw RuntimeException when album ID does not exist")
    public void shouldThrowRuntimeExceptionWhenAlbumIdDoesNotExist() {
        Long albumId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> albumService.deleteAlbum(albumId));
    }

    @Test
    @DisplayName("Should return album list when given a valid user")
    public void shouldReturnAlbumListWhenUserIsValid(){
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Users users = Users.builder()
                .id(1L)
                .email("test@email.com")
                .password("test")
                .build();

        Album album1 = Album.builder().id(1L).build();
        Album album2 = Album.builder().id(2L).build();
        Album album3 = Album.builder().id(3L).build();


        List<Album> albumList = Arrays.asList(album1, album2, album3);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(users.getEmail());
        when(usersService.findByEmail(users.getEmail())).thenReturn(users);

        when(albumRepository.findAllByUsers(users)).thenReturn(albumList);

        List<Album> retrievedAlbumList = albumService.getUserAlbums();

        assertEquals(albumList, retrievedAlbumList);
    }


}
