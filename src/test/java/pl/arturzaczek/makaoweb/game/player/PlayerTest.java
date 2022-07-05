package pl.arturzaczek.makaoweb.game.player;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.arturzaczek.makaoweb.game.cards.*;
import pl.arturzaczek.makaoweb.utils.CardHelper;

import java.util.List;

class PlayerTest {

    @Test
    public void shouldReturnTrueWhenHasAny2ValCardOnHand() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(List.of(CardHelper.S2.getCard(), new Diamond("5"), new Heart("5"), new Spade("Jack"), new Spade("5")))
                .build();
        Assertions.assertTrue(player.has2OnHand());
    }

    @Test
    public void shouldReturnTrueWhenHasAny3ValCardOnHand() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(List.of(CardHelper.S3.getCard(), new Diamond("5"), new Heart("5"), new Spade("Jack"), new Spade("5")))
                .build();
        Assertions.assertTrue(player.has3OnHand());
    }

    @Test
    public void shouldReturnFalseWhenHasAny3ValCardOnHandButTestOn2() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(List.of(CardHelper.S3.getCard(), new Diamond("5"), new Heart("5"), new Spade("Jack"), new Spade("5")))
                .build();
        Assertions.assertFalse(player.has2OnHand());
    }

    @Test
    public void shouldReturnTrueWhenHasAny4ValCardOnHand() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(List.of(CardHelper.S4.getCard(), new Diamond("5"), new Heart("5"), new Spade("Jack"), new Spade("5")))
                .build();
        Assertions.assertTrue(player.has4OnHand());
    }

    @Test
    public void shouldReturnFalseWhenPlayerHasNoFunctionalCard() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(CardHelper.NON_FUNCTIONAL_RANDOM4.getCards())
                .build();
        Assertions.assertTrue(!player.has2OnHand() || !player.has4OnHand() || !player.has3OnHand());
    }


    @Test
    public void shouldRemoveCard() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(CardHelper.NON_FUNCTIONAL_RANDOM4.getCards())
                .build();
        final List<BaseCard> expected = List.of(new Club("5"), new Heart("5"), new Spade("6"));
        player.removeCardsFromHand(List.of(new Heart("10")));
        Assertions.assertEquals(expected, player.getOnHand());
    }

    @Test
    public void shouldRemove2Cards() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(CardHelper.NON_FUNCTIONAL_RANDOM4.getCards())
                .build();
        final List<BaseCard> expected = List.of(new Club("5"), new Spade("6"));
        player.removeCardsFromHand(List.of(new Heart("10"), new Heart("5")));
        Assertions.assertEquals(expected, player.getOnHand());
    }

    @Test
    public void shouldReturnAndDecrementMovements() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(CardHelper.NON_FUNCTIONAL_RANDOM4.getCards())
                .movementsBlocked(3)
                .build();

        Assertions.assertEquals(3, player.getMovementsBlockedAndDecrement());
        Assertions.assertEquals(2, player.getMovementsBlocked());
    }

    @Test
    public void shouldReturnMovements() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(CardHelper.NON_FUNCTIONAL_RANDOM4.getCards())
                .movementsBlocked(0)
                .build();
        Assertions.assertEquals(0, player.getMovementsBlockedAndDecrement());
    }

    @Test
    public void shouldReturnMovementsBlockedAndUnlockPlayer() {
        final Player player = Player.builder()
                .name("abc")
                .uuid("test-123")
                .onHand(CardHelper.NON_FUNCTIONAL_RANDOM4.getCards())
                .state(Player.State.BLOCKED)
                .movementsBlocked(1)
                .build();
        Assertions.assertEquals(1, player.getMovementsBlockedAndDecrement());
        Assertions.assertEquals(0, player.getMovementsBlocked());
        Assertions.assertEquals(Player.State.WAITING, player.getState());
    }
}