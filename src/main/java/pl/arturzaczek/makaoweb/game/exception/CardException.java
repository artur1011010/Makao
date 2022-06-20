package pl.arturzaczek.makaoweb.game.exception;

public class CardException extends RuntimeException{
    private String details;

    public CardException(String message, String details) {
        super(message);
        this.details = details;
    }

    public CardException(String details) {
        this.details = details;
    }
}
