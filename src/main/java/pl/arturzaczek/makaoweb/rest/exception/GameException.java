package pl.arturzaczek.makaoweb.rest.exception;

public class GameException extends BaseGameException{
    public GameException(String message, String code) {
        super(message, code);
    }

    public GameException(String message, String code, String details) {
        super(message, code, details);
    }
}
