package pl.arturzaczek.makaoweb.game.exception;

public class PlayerException extends RuntimeException {
    private String details;

    public PlayerException(String message, String details) {
        super(message);
        this.details = details;
    }

    public PlayerException(String details) {
        this.details = details;
    }
}
