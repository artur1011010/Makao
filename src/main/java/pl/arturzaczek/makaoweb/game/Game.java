package pl.arturzaczek.makaoweb.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pl.arturzaczek.makaoweb.game.cards.CardDeck;
import pl.arturzaczek.makaoweb.game.exception.GameException;
import pl.arturzaczek.makaoweb.game.exception.PlayerException;
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

    public void startGame() {
        if (playerList.size() < 2) {
            log.error("Too few players");
            return;
        } else if (gameState != Game.GameState.OPEN) {
            log.error("incorrect game state");
            return;
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

    public boolean playerJoin(final Player player) {
        log.info("Player {} with uuid: {} join", player.getName(), player.getUuid());
        if (playerList.size() < 4 && gameState == GameState.OPEN && player.getState() == Player.State.IDLE) {
            playerList.add(player);
            player.setState(Player.State.WAITING);
            log.info("player: {} added to game", player);
            return true;
        } else {
            log.error("incorrect game state or too much players");
            throw new GameException("incorrect game state or too much players");
        }
    }

    public Player getPlayerByUuid(final String uuid) {
        return playerList.stream()
                .filter(player1 -> player1.getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new PlayerException("user with uuid: " + uuid + " not found"));
    }

    public Player getPrevPlayerByCurrentUuid(final String uuid) {
        final Player current = getPlayerByUuid(uuid);
        final int currentPosition = playerList.indexOf(current);
        if (currentPosition < 0) {
            log.error("can not find current player position");
            return new Player();
        }
        if (currentPosition == 0) {
            return playerList.get(playerList.size() - 1);
        }
        return playerList.get(currentPosition - 1);
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

    private void firstDeal() {
        playerList.forEach(player -> player.setOnHand(cardDeck.getNextCard(5)));
        cardDeck.putFirstCardAway();
        setFirstPlayerActive();
    }

    private void setFirstPlayerActive() {
        playerList.get(0).setState(Player.State.ACTIVE);
    }


    private String getNextPlayerUuidByCurrentPlayer(Player player) {
        final int currentPosition = playerList.indexOf(player);
        if (currentPosition < 0) {
            log.error("can not find current player position");
            return StringUtils.EMPTY;
        }
        if (currentPosition > 0 && playerList.size() - 1 == currentPosition) {
            return playerList.get(0).getUuid();
        }
        return playerList.get(currentPosition + 1).getUuid();
    }


    private String getPervPlayerUuidByCurrentPlayer(Player player) {
        final int currentPosition = playerList.indexOf(player);
        if (currentPosition < 0) {
            log.error("can not find current player position");
            return StringUtils.EMPTY;
        }
        if (currentPosition == 0) {
            return playerList.get(playerList.size() - 1).getUuid();
        }
        return playerList.get(currentPosition + 1).getUuid();
    }

    @AllArgsConstructor
    @Getter
    public enum GameState {
        OPEN,
        PLAYING,
        FINISHED
    }
}
