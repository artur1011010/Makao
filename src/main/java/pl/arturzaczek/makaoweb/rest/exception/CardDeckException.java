package pl.arturzaczek.makaoweb.rest.exception;

public class CardDeckException extends BaseGameException{
    public CardDeckException(String message, String code) {
        super(message, code);
    }

    public CardDeckException(String message, String code, String details) {
        super(message, code, details);
    }
}
