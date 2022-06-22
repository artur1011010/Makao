package pl.arturzaczek.makaoweb.game.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseGameException extends RuntimeException {
    protected String details;

    public BaseGameException(String message) {
        super(message);
    }

    public BaseGameException(String message, String details) {
        super(message);
        this.details = details;
    }
}
