package pl.arturzaczek.makaoweb.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.arturzaczek.makaoweb.game.player.Player;
import pl.arturzaczek.makaoweb.utils.CardHelper;

import java.util.List;

class CardDeckTest {

    @Test
//    @Disabled("disabled due to test only functional cards ")
    public void shouldRollOverCardDeck() {
        CardDeck cardDeck = new CardDeck();
        cardDeck.getDeck().clear();
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(List.of(CardHelper.S2.getCard()))
                .build();
        Assertions.assertTrue(player.has2OnHand());
        cardDeck.setLastOnStack(CardHelper.C2.getCard());
        cardDeck.rollOverCardDeck(List.of(player));
        Assertions.assertEquals(50, cardDeck.getDeck().size());
        Assertions.assertFalse(cardDeck.getDeck().contains(CardHelper.C2.getCard()));
        Assertions.assertFalse(cardDeck.getDeck().contains(CardHelper.S2.getCard()));
    }
}