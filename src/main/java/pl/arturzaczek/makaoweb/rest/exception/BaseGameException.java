package pl.arturzaczek.makaoweb.rest.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseGameException extends RuntimeException {
    protected String code;
    protected String details;

    public BaseGameException(String message, String code) {
        super(message);
        this.code = code;
    }

    public BaseGameException(String message, String code, String details) {
        super(message);
        this.code = code;
        this.details = details;
    }
}
