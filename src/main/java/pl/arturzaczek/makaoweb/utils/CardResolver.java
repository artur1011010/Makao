package pl.arturzaczek.makaoweb.utils;

import org.springframework.stereotype.Component;
import pl.arturzaczek.makaoweb.game.cards.BaseCard;
import pl.arturzaczek.makaoweb.game.exception.CardException;
import pl.arturzaczek.makaoweb.rest.dto.CardDto;

@Component
public class CardResolver {

    public CardDto getCardDto(final BaseCard baseCard) {
        if (baseCard == null) {
            return null;
        }
        final String simpleName = baseCard.getClass().getSimpleName();
        switch (simpleName) {
            case "Diamond":
                return new CardDto(baseCard.getValue(), "Diamond");
            case "Heart":
                return new CardDto(baseCard.getValue(), "Heart");
            case "Club":
                return new CardDto(baseCard.getValue(), "Club");
            case "Spade":
                return new CardDto(baseCard.getValue(), "Spade");
        }
        throw new CardException("can not resolve card type");
    }
}
