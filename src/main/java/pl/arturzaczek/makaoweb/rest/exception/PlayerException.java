package pl.arturzaczek.makaoweb.rest.exception;

public class PlayerException extends BaseGameException{
    public PlayerException(String message, String code) {
        super(message, code);
    }

    public PlayerException(String message, String code, String details) {
        super(message, code, details);
    }
}
