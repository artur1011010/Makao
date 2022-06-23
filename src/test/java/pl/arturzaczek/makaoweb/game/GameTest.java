package pl.arturzaczek.makaoweb.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.arturzaczek.makaoweb.game.cards.Diamond;
import pl.arturzaczek.makaoweb.game.cards.Heart;
import pl.arturzaczek.makaoweb.game.cards.Spade;
import pl.arturzaczek.makaoweb.game.exception.GameException;
import pl.arturzaczek.makaoweb.game.player.Player;
import pl.arturzaczek.makaoweb.utils.CardHelper;
import pl.arturzaczek.makaoweb.utils.CardValues;

import java.util.ArrayList;
import java.util.List;

class GameTest {

    @Test
    public void shouldThrowGameExceptionWhenAddPlayerAndStateIsPlaying() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final GameException exception = Assertions.assertThrows(GameException.class, () -> game.playerJoin(new Player()));
        Assertions.assertEquals(3, game.getPlayerList().size());
        Assertions.assertEquals("incorrect game state or too much players", exception.getMessage());
    }

    @Test
    public void shouldAddNewPlayerWhenAddPlayerAndStateIsOpen() {
        final Game game = mockGame(Game.GameState.OPEN);
        Assertions.assertDoesNotThrow(() -> game.playerJoin(new Player()));
        Assertions.assertEquals(4, game.getPlayerList().size());
    }

    @Test
    public void shouldReturnPlayer2ByUuid() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final Player player = game.getPlayerByUuid("test2");

        final Player expected = Player.builder()
                .name("player2")
                .uuid("test2")
                .state(Player.State.WAITING)
                .onHand(List.of(new Diamond(CardValues._10.getValue()), new Spade(CardValues._6.getValue()), new Spade(CardValues._7.getValue()), new Spade(CardValues._9.getValue())))
                .build();
        Assertions.assertEquals(expected, player);
    }

    @Test
    public void shouldReturnNextPlayer() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final Player player = game.getNextPlayerByCurrentUuid("test2");

        final Player expected = Player.builder()
                .name("player3")
                .uuid("test3")
                .state(Player.State.ACTIVE)
                .onHand(List.of(new Heart(CardValues._10.getValue()), new Spade(CardValues._10.getValue()), new Spade(CardValues._5.getValue()), new Heart(CardValues._9.getValue())))
                .build();

        Assertions.assertEquals(expected, player);
    }

    @Test
    public void shouldReturnPlayer3ByUuid() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final Player player = game.getPlayerByUuid("test3");

        final Player expected = Player.builder()
                .name("player3")
                .uuid("test3")
                .state(Player.State.ACTIVE)
                .onHand(List.of(new Heart(CardValues._10.getValue()), new Spade(CardValues._10.getValue()), new Spade(CardValues._5.getValue()), new Heart(CardValues._9.getValue())))
                .build();
        Assertions.assertEquals(expected, player);
    }

    @Test
    public void shouldReturnNextPlayerByUuid() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final Player player = game.getNextPlayerByCurrentUuid("test3");
        final Player expected = Player.builder()
                .name("player1")
                .uuid("test1")
                .state(Player.State.WAITING)
                .onHand(List.of(CardHelper.S2.getCard(), new Diamond(CardValues._5.getValue()), new Heart(CardValues._5.getValue()), new Spade(CardValues._Jack.getValue()), new Spade(CardValues._6.getValue())))
                .build();
        Assertions.assertEquals(expected, player);
    }

    @Test
    public void shouldReturnNextPlayerByUuid2() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final Player player = game.getNextPlayerByCurrentUuid("test2");
        final Player expected = Player.builder()
                .name("player3")
                .uuid("test3")
                .state(Player.State.WAITING)
                .onHand(List.of(new Heart(CardValues._10.getValue()), new Spade(CardValues._10.getValue()), new Spade(CardValues._5.getValue()), new Heart(CardValues._9.getValue())))
                .build();
        Assertions.assertEquals(expected, player);
    }

    @Test
    public void shouldReturnPrevPlayerByUuid() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final Player player = game.getPrevPlayerByCurrentUuid("test3");
        final Player expected = Player.builder()
                .name("player2")
                .uuid("test2")
                .state(Player.State.WAITING)
                .onHand(List.of(new Diamond(CardValues._10.getValue()), new Spade(CardValues._6.getValue()), new Spade(CardValues._7.getValue()), new Spade(CardValues._9.getValue())))
                .build();
        Assertions.assertEquals(expected, player);
    }

    @Test
    public void shouldReturnPrevPlayerByUuid2() {
        final Game game = mockGame(Game.GameState.PLAYING);
        final Player player = game.getPrevPlayerByCurrentUuid("test1");
        final Player expected = Player.builder()
                .name("player3")
                .uuid("test3")
                .state(Player.State.ACTIVE)
                .onHand(List.of(new Heart(CardValues._10.getValue()), new Spade(CardValues._10.getValue()), new Spade(CardValues._5.getValue()), new Heart(CardValues._9.getValue())))
                .build();

        Assertions.assertEquals(expected, player);
    }

    @Test
    public void shouldStartGame() {
        final Game game = mockGame(Game.GameState.OPEN);
        Assertions.assertDoesNotThrow(game::startGame);
        final Player player1 = Player.builder()
                .name("player1")
                .uuid("test1")
                .state(Player.State.ACTIVE)
                .onHand(List.of(CardHelper.S2.getCard(), new Diamond(CardValues._5.getValue()), new Heart(CardValues._5.getValue()), new Spade(CardValues._Jack.getValue()), new Spade(CardValues._6.getValue())))
                .build();
        Assertions.assertEquals(player1, game.getPlayerByUuid("test1"));
        Assertions.assertEquals(Game.GameState.PLAYING, game.getGameState());
    }

    @Test
    public void shouldStartGameAndDealCardsBetweenPlayers() {
        final Game game = mockGame(Game.GameState.OPEN);
        Assertions.assertDoesNotThrow(game::startGame);
        final Player player1 = game.getPlayerByUuid("test1");
        final Player player2 = game.getPlayerByUuid("test2");
        final Player player3 = game.getPlayerByUuid("test3");
        Assertions.assertEquals(5, player1.getOnHand().size());
        Assertions.assertEquals(5, player2.getOnHand().size());
        Assertions.assertEquals(5, player3.getOnHand().size());
        Assertions.assertEquals(Game.GameState.PLAYING, game.getGameState());
    }

    @Test
    public void shouldToggleNextPlayerActive() {
        final Game game = mockGame(Game.GameState.OPEN);
        final Player player1 = game.getPlayerByUuid("test1");
        Assertions.assertDoesNotThrow(
                ()-> game.toggleNextPlayerActive(player1));

        final Player player2 = game.getPlayerByUuid("test2");
        Assertions.assertEquals(Player.State.ACTIVE, player2.getState());
        Assertions.assertEquals(Player.State.WAITING, player1.getState());
    }

    public Game mockGame(final Game.GameState state) {
        final Player player1 = Player.builder()
                .name("player1")
                .uuid("test1")
                .state(Player.State.WAITING)
                .onHand(state == Game.GameState.PLAYING ? List.of(CardHelper.S2.getCard(), new Diamond(CardValues._5.getValue()), new Heart(CardValues._5.getValue()), new Spade(CardValues._Jack.getValue()), new Spade(CardValues._6.getValue())) : new ArrayList<>())
                .build();

        final Player player2 = Player.builder()
                .name("player2")
                .uuid("test2")
                .state(Player.State.WAITING)
                .onHand(state == Game.GameState.PLAYING ? List.of(new Diamond(CardValues._10.getValue()), new Spade(CardValues._6.getValue()), new Spade(CardValues._7.getValue()), new Spade(CardValues._9.getValue())) : new ArrayList<>())
                .build();

        final Player player3 = Player.builder()
                .name("player3")
                .uuid("test3")
                .state(state == Game.GameState.PLAYING ? Player.State.ACTIVE : Player.State.WAITING)
                .onHand(state == Game.GameState.PLAYING ? List.of(new Heart(CardValues._10.getValue()), new Spade(CardValues._10.getValue()), new Spade(CardValues._5.getValue()), new Heart(CardValues._9.getValue())) : new ArrayList<>())
                .build();

        Game game = new Game();
        game.setPlayerList(new ArrayList<>(List.of(player1, player2, player3)));
        game.setGameState(state);
        return game;
    }

}