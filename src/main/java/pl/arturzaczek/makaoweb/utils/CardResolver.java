package pl.arturzaczek.makaoweb.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.arturzaczek.makaoweb.game.cards.*;
import pl.arturzaczek.makaoweb.game.exception.CardException;
import pl.arturzaczek.makaoweb.rest.dto.CardDto;

@Component
@Slf4j
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

    public BaseCard getBaseCard(final CardDto cardDto) {
        if (cardDto == null) {
            return null;
        }

        final String simpleName = cardDto.getColor();
        switch (simpleName) {
            case "Diamond":
                return new Diamond(cardDto.getValue());
            case "Heart":
                return new Heart(cardDto.getValue());
            case "Club":
                return new Club(cardDto.getValue());
            case "Spade":
                return new Spade(cardDto.getValue());
        }
        throw new CardException("can not resolve card type");
    }
}
