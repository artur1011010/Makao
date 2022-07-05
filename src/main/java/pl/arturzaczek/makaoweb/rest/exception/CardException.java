package pl.arturzaczek.makaoweb.rest.exception;

public class CardException extends BaseGameException{
    public CardException(String message, String code) {
        super(message, code);
    }

    public CardException(String message, String code, String details) {
        super(message, code, details);
    }
}
