package pl.arturzaczek.makaoweb.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.arturzaczek.makaoweb.rest.exception.GameException;
import pl.arturzaczek.makaoweb.game.player.Player;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Getter
@Setter
public class Game {

    private List<Player> playerList = new ArrayList<>();
    private CardDeck cardDeck = new CardDeck();
    private GameState gameState = GameState.OPEN;
    private int functionalCards;

    public void startGame() {
        if (playerList.size() < 2 || playerList.size() > 3) {
            log.error("Wrong number of players");
            throw new GameException("Wrong number of players", HttpStatus.BAD_REQUEST.toString());
        } else if (gameState != Game.GameState.OPEN) {
            log.error("Incorrect game state");
            throw new GameException("Incorrect game state", HttpStatus.BAD_REQUEST.toString());
        }
        gameState = GameState.PLAYING;
        firstDeal();
    }

    public void restartGame() {
        if (playerList.size() < 2) {
            log.error("Too few players");
            return;
        }
        cardDeck = new CardDeck();
        gameState = GameState.PLAYING;
        playerList.forEach(player -> {
            player.setOnHand(new ArrayList<>());
            player.setState(Player.State.WAITING);
        });
        firstDeal();
    }

    public void setGameStateFinishedAndClearTable() {
        gameState = GameState.FINISHED;
        cardDeck = new CardDeck();
        playerList.forEach(player -> {
            player.setOnHand(new ArrayList<>());
            player.setState(Player.State.WAITING);
        });
    }

    public void addCardsToPlayerHand(final int amount, final Player player) {
        player.getOnHand().addAll(cardDeck.getNextCard(amount, playerList));
    }

    public boolean playerJoin(final Player player) {
        log.info("Player {} with uuid: {} join", player.getName(), player.getUuid());
        if (playerList.size() < 4 && gameState == GameState.OPEN && player.getState() == Player.State.IDLE) {
            playerList.add(player);
            player.setState(Player.State.WAITING);
            log.info("player: {} added to game", player);
            return true;
        } else {
            log.error("incorrect game state or too much players");
            throw new GameException("incorrect game state or too much players", HttpStatus.BAD_REQUEST.toString());
        }
    }

    public Player getPlayerByUuid(final String uuid) {
        return playerList.stream()
                .filter(player1 -> player1.getUuid().equals(uuid))
                .findFirst()
                .orElse(new Player());
    }

    public Player getNextPlayerByCurrentUuid(final String uuid) {
        final Player current = getPlayerByUuid(uuid);
        final int currentPosition = playerList.indexOf(current);
        if (currentPosition < 0) {
            log.error("can not find current player position");
            return new Player();
        }
        if (currentPosition > 0 && playerList.size() - 1 == currentPosition) {
            return playerList.get(0);
        }
        return playerList.get(currentPosition + 1);
    }

    public Player getNextWaitingPlayerAndSetActiveByCurrentUuid(final String currentUuid) {
        Player next = getNextPlayerByCurrentUuid(currentUuid);
        if (next.getMovementsBlockedAndDecrement() != 0) {
            do {
                next = getNextPlayerByCurrentUuid(next.getUuid());
                next.getMovementsBlockedAndDecrement();
            } while (next.getState() != Player.State.WAITING && next.getMovementsBlocked() != 0);
        }
        next.setState(Player.State.ACTIVE);
        return next;
    }

    private void firstDeal() {
        playerList.forEach(player -> player.setOnHand(cardDeck.getNextCard(5, playerList)));
        cardDeck.putFirstCardAway();
        setFirstPlayerActive();
    }

    private void setFirstPlayerActive() {
        playerList.get(0).setState(Player.State.ACTIVE);
    }

    @Deprecated
    public void toggleNextWaitingPlayerActive(final Player currentPlayer) {
        currentPlayer.setState(Player.State.WAITING);
        final Player nextPlayer = getNextWaitingPlayerAndSetActiveByCurrentUuid(currentPlayer.getUuid());
        nextPlayer.setState(Player.State.ACTIVE);
    }

    public String getActivePlayerUuid() {
        return playerList.stream()
                .filter(player -> player.getState() == Player.State.ACTIVE)
                .map(Player::getUuid)
                .findFirst()
                .orElse("no active player");
    }

    public Player getWinner() {
        return playerList.stream()
                .filter(player -> player.getOnHand().isEmpty() && player.getState() != Player.State.IDLE)
                .findFirst()
                .orElse(null);
    }

    @AllArgsConstructor
    @Getter
    public enum GameState {
        OPEN,
        PLAYING,
        FINISHED
    }
}
