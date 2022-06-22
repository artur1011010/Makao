package pl.arturzaczek.makaoweb.game.cards;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.arturzaczek.makaoweb.game.exception.CardDeckException;
import pl.arturzaczek.makaoweb.utils.CardValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Slf4j
public class CardDeck {

    private static final List<String> values = Arrays.stream(CardValues.values()).map(CardValues::getValue).collect(Collectors.toList());

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
        final String lastOnStackClass = lastOnStack.getClass().getSimpleName();
        if(lastOnStackClass.equals(card.getClass().getSimpleName())){
            afterDeal.add(lastOnStack);
            lastOnStack = card;
        }else if(lastOnStack.getValue().equals(card.getValue())){
            afterDeal.add(lastOnStack);
            lastOnStack = card;
        }else {
            log.error("karta nie moze zostać odłozona");
        }
    }

    public void putFirstCardAway(){
        lastOnStack = getNextCard();
    }

}
