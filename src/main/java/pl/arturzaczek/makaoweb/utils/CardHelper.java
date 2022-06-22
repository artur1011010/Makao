package pl.arturzaczek.makaoweb.utils;

import lombok.Getter;
import pl.arturzaczek.makaoweb.game.cards.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum CardHelper {
    D2(new Diamond(BaseCard.VALUE_2)),
    S2(new Spade(BaseCard.VALUE_2)),
    H2(new Heart(BaseCard.VALUE_2)),
    C2(new Club(BaseCard.VALUE_2)),
    D3(new Diamond(BaseCard.VALUE_3)),
    S3(new Spade(BaseCard.VALUE_3)),
    H3(new Heart(BaseCard.VALUE_3)),
    C3(new Club(BaseCard.VALUE_3)),
    D4(new Diamond(BaseCard.VALUE_4)),
    S4(new Spade(BaseCard.VALUE_4)),
    H4(new Heart(BaseCard.VALUE_4)),
    C4(new Club(BaseCard.VALUE_4)),
    ALL4(List.of(new Club(BaseCard.VALUE_4), new Diamond(BaseCard.VALUE_4), new Spade(BaseCard.VALUE_4), new Heart(BaseCard.VALUE_4))),
    ALL3(List.of(new Club(BaseCard.VALUE_3), new Diamond(BaseCard.VALUE_3), new Spade(BaseCard.VALUE_3), new Heart(BaseCard.VALUE_3))),
    ALL2(List.of(new Club(BaseCard.VALUE_2), new Diamond(BaseCard.VALUE_2), new Spade(BaseCard.VALUE_2), new Heart(BaseCard.VALUE_2))),
    NON_FUNCTIONAL_RANDOM4(new ArrayList<>(List.of(new Club("5"), new Heart("10"), new Heart("5"), new Spade("6"))));


    private List<BaseCard> cards;

    private BaseCard card;

    CardHelper(List<BaseCard> cards) {
        this.cards = cards;
    }

    CardHelper(BaseCard card) {
        this.card = card;
    }
}
