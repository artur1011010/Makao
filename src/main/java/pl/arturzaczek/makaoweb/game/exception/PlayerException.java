package pl.arturzaczek.makaoweb.game.exception;

public class PlayerException extends BaseGameException{
    public PlayerException(String message) {
        super(message);
    }

    public PlayerException(String message, String details) {
        super(message, details);
    }
}
