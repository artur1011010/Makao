package pl.arturzaczek.makaoweb.game;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.arturzaczek.makaoweb.game.cards.*;
import pl.arturzaczek.makaoweb.game.player.Player;
import pl.arturzaczek.makaoweb.utils.CardValues;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Slf4j
public class CardDeck {

    private static final List<String> values = Arrays.stream(CardValues.values())
            .map(CardValues::getValue)
            .collect(Collectors.toList());

    private final List<BaseCard> deck = new ArrayList<>();
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

    public List<BaseCard> getNextCard(final int n, final List<Player> playerList) {
        if (deck.size() < n) {
            rollOverCardDeck(playerList);
        }
        final List<BaseCard> cardsToRemove = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cardsToRemove.add(deck.remove(0));
        }
        return cardsToRemove;
    }

    void rollOverCardDeck(final List<Player> playerList) {
        List<BaseCard> cards = new ArrayList<>();
        final List<BaseCard> cardsInUse = Stream.concat(playerList.stream()
                        .map(Player::getOnHand)
                        .flatMap(Collection::stream),
                List.of(lastOnStack).stream())
                .collect(Collectors.toList());
        values.forEach(value -> {
            cards.addAll(List.of(Diamond.builder().value(value).build(),
                    Club.builder().value(value).build(),
                    Spade.builder().value(value).build(),
                    Heart.builder().value(value).build()));
        });
        cards.stream()
                .filter(card -> !cardsInUse.contains(card))
                .forEach(deck::add);
    }

    public void putFirstCardAway() {
        lastOnStack = deck.remove(0);
    }

}
