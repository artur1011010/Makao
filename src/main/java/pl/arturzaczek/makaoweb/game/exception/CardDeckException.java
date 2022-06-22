package pl.arturzaczek.makaoweb.game.exception;

public class CardDeckException extends BaseGameException{
    public CardDeckException(String message) {
        super(message);
    }

    public CardDeckException(String message, String details) {
        super(message, details);
    }
}
