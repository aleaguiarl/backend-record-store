package br.com.sysmap.bootcamp.domain.service.integration;

import br.com.sysmap.bootcamp.domain.mapper.AlbumMapper;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SpotifyApi {

    private final se.michaelthelin.spotify.SpotifyApi spotifyApi = new se.michaelthelin.spotify.SpotifyApi.Builder()
            .setClientId("7a7236214d6545f1834f9d36505c8906")
            .setClientSecret("069014690481490482eb3ece409ab0f8")
            .build();

    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        spotifyApi.setAccessToken(getToken());

        return addRandomAlbumValues(AlbumMapper.INSTANCE.toModel(spotifyApi.searchAlbums(search).market(CountryCode.BR)
                .limit(30)
                .build().execute().getItems()));
    }

    private List<AlbumModel> addRandomAlbumValues(List<AlbumModel> albumModelList) {
        return albumModelList.stream()
                .peek(album -> album.setValue(BigDecimal.valueOf(Math.random() * (100))
                        .setScale(2, RoundingMode.HALF_UP))).toList();
    }

    public String getToken() throws IOException, ParseException, SpotifyWebApiException {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        return clientCredentialsRequest.execute().getAccessToken();
    }

}
