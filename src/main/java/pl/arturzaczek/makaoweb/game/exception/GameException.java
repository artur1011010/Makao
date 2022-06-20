package pl.arturzaczek.makaoweb.game.exception;

public class GameException extends RuntimeException {
    private String details;

    public GameException(String message, String details) {
        super(message);
        this.details = details;
    }

    public GameException(String details) {
        this.details = details;
    }
}
