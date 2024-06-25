package br.com.sysmap.bootcamp.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlbumAlreadyBoughtException extends RuntimeException {
    public AlbumAlreadyBoughtException(String message) {
        super(message);
    }

    public AlbumAlreadyBoughtException(String message, Throwable cause) {
        super(message, cause);
    }
}
