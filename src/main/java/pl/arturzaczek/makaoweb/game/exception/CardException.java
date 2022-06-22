package pl.arturzaczek.makaoweb.game.exception;

public class CardException extends BaseGameException{
    public CardException(String message) {
        super(message);
    }

    public CardException(String message, String details) {
        super(message, details);
    }
}
