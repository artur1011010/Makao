package pl.arturzaczek.makaoweb.game.exception;

public class GameException extends BaseGameException{
    public GameException(String message) {
        super(message);
    }

    public GameException(String message, String details) {
        super(message, details);
    }
}
