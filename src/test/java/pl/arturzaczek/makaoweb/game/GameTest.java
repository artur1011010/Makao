package pl.arturzaczek.makaoweb.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.arturzaczek.makaoweb.game.cards.Diamond;
import pl.arturzaczek.makaoweb.game.cards.Heart;
import pl.arturzaczek.makaoweb.game.cards.Spade;
import pl.arturzaczek.makaoweb.rest.exception.GameException;
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
        Assertions.assertEquals("400 BAD_REQUEST", exception.getCode());
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
    public void shouldThrowGameExceptionWhenTooMuchPlayersOnStartGame() {
        Game game = new Game();
        game.setPlayerList(new ArrayList<>(List.of(Player.builder().build(), Player.builder().build(), Player.builder().build(), Player.builder().build())));
        game.setGameState(Game.GameState.OPEN);
        final GameException exception = Assertions.assertThrows(GameException.class, game::startGame);
        Assertions.assertEquals("Wrong number of players", exception.getMessage());
    }

    @Test
    public void shouldThrowGameExceptionWhenTooLittlePlayersOnStartGame() {
        Game game = new Game();
        game.setPlayerList(new ArrayList<>(List.of(Player.builder().build())));
        game.setGameState(Game.GameState.OPEN);
        final GameException exception = Assertions.assertThrows(GameException.class, game::startGame);
        Assertions.assertEquals("Wrong number of players", exception.getMessage());
        Assertions.assertEquals("400 BAD_REQUEST", exception.getCode());
    }

    @Test
    public void shouldThrowGameExceptionWhenGameHasIncorrectStateOnStartGame() {
        Game game = new Game();
        game.setPlayerList(new ArrayList<>(List.of(Player.builder().build(), Player.builder().build(), Player.builder().build())));
        game.setGameState(Game.GameState.PLAYING);
        final GameException exception = Assertions.assertThrows(GameException.class, game::startGame);
        Assertions.assertEquals("Incorrect game state", exception.getMessage());
        Assertions.assertEquals("400 BAD_REQUEST", exception.getCode());
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
    public void shouldReturnNextWaitingPlayer() {
        final Player player1 = Player.builder()
                .uuid("test1")
                .state(Player.State.ACTIVE)
                .build();

        final Player player2 = Player.builder()
                .uuid("test2")
                .state(Player.State.BLOCKED)
                .movementsBlocked(2)
                .build();

        final Player player3 = Player.builder()
                .uuid("test3")
                .state(Player.State.WAITING)
                .build();

        Game game = new Game();
        game.setPlayerList(new ArrayList<>(List.of(player1, player2, player3)));
        game.setGameState(Game.GameState.PLAYING);


        final Player actual = Assertions.assertDoesNotThrow(
                () -> game.getNextWaitingPlayerAndSetActiveByCurrentUuid("test1"));
        final Player expected = game.getPlayerByUuid("test3");
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(1, player2.getMovementsBlocked());
        Assertions.assertEquals(Player.State.BLOCKED, player2.getState());
        Assertions.assertEquals(Player.State.ACTIVE, actual.getState());
    }

    @Test
    public void shouldReturnNextWaitingPlayerWhenCurrentIsLast() {
        final Player player1 = Player.builder()
                .uuid("test1")
                .state(Player.State.WAITING)
                .build();

        final Player player2 = Player.builder()
                .uuid("test2")
                .state(Player.State.ACTIVE)
                .movementsBlocked(0)
                .build();

        final Player player3 = Player.builder()
                .uuid("test3")
                .state(Player.State.BLOCKED)
                .movementsBlocked(1)
                .build();

        Game game = new Game();
        game.setPlayerList(new ArrayList<>(List.of(player1, player2, player3)));
        game.setGameState(Game.GameState.PLAYING);


        final Player actual = Assertions.assertDoesNotThrow(
                () -> game.getNextWaitingPlayerAndSetActiveByCurrentUuid("test2"));
        final Player expected = game.getPlayerByUuid("test1");
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, player3.getMovementsBlocked());
        Assertions.assertEquals(Player.State.WAITING, player3.getState());
        Assertions.assertEquals(Player.State.ACTIVE, actual.getState());
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