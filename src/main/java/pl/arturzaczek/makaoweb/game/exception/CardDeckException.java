package pl.arturzaczek.makaoweb.game.exception;

public class CardDeckException extends RuntimeException{
    private String details;

    public CardDeckException(String message, String details) {
        super(message);
        this.details = details;
    }

    public CardDeckException(String details) {
        this.details = details;
    }
}
