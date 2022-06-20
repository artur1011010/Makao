package pl.arturzaczek.makaoweb.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.arturzaczek.makaoweb.game.cards.CardDeck;
import pl.arturzaczek.makaoweb.game.exception.GameException;
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
        } else if (gameState != Game.GameState.OPEN) {
            log.error("incorrect game state");
            return;
        }
        cardDeck = new CardDeck();
        gameState = GameState.PLAYING;
        playerList.forEach(player -> player.setOnHand(new ArrayList<>()));
        firstDeal();
    }

    //create new player
    public boolean playerJoin(final Player player) {
        log.error("{}", player.getUuid());
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

    private void firstDeal() {
        playerList.forEach(player -> player.setOnHand(cardDeck.getNextCard(5)));
        cardDeck.putFirstCardAway();
        setFirstPlayerActive();
    }

    private void setFirstPlayerActive() {
        playerList.get(0).setState(Player.State.ACTIVE);
    }

    private int nextPlayerIndex(int currentPlayerIndex) {
        if (currentPlayerIndex < playerList.size() - 1) {
            return ++currentPlayerIndex;
        } else
            return 0;
    }


    @AllArgsConstructor
    @Getter
    public enum GameState {
        OPEN,
        PLAYING,
        FINISHED
    }
}
