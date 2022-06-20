package pl.arturzaczek.makaoweb.game.cards;

import lombok.Getter;
import lombok.Setter;
import pl.arturzaczek.makaoweb.game.exception.CardDeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class CardDeck {

    private static final List<String> values = List.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace");

    private final List<BaseCard> deck = new ArrayList<>();
    private final List<BaseCard> afterDeal = new ArrayList<>();
    private BaseCard lastOnStack;


    public CardDeck() {
        populateCardDeck();
        shuffle();
    }

    private void populateCardDeck() {
        values.forEach(value -> {
            deck.addAll(List.of(Diamond.builder().value(value).build(),
                    Club.builder().value(value).build(),
                    Spade.builder().value(value).build(),
                    Heart.builder().value(value).build()));
        });
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public BaseCard getNextCard() {
        if (deck == null || deck.isEmpty()) {
            throw new CardDeckException("card deck is empty");
        }
        return deck.remove(0);
    }

    public List<BaseCard> getNextCard(int n) {
        if (deck == null || deck.isEmpty() || deck.size() < n) {
            throw new CardDeckException("card deck has to little cards");
        }
        final List<BaseCard> cardsToRemove = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cardsToRemove.add(deck.remove(0));
        }
        return cardsToRemove;
    }

    public void putCardAway(final BaseCard card) {
        afterDeal.add(lastOnStack);
        lastOnStack = card;
    }

    public void putFirstCardAway(){
        lastOnStack = getNextCard();
    }

}
