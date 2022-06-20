package pl.arturzaczek.makaoweb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pl.arturzaczek.makaoweb.game.Game;
import pl.arturzaczek.makaoweb.game.cards.CardDeck;
import pl.arturzaczek.makaoweb.game.exception.PlayerException;
import pl.arturzaczek.makaoweb.game.player.Player;
import pl.arturzaczek.makaoweb.rest.dto.GameStateDto;
import pl.arturzaczek.makaoweb.rest.dto.PlayerDto;
import pl.arturzaczek.makaoweb.rest.dto.PlayerStateDto;
import pl.arturzaczek.makaoweb.utils.CardResolver;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {

    private final Game game;
    private final CardResolver cardResolver;

    public Pair<String, String> createPlayerAndJoinGame(final String name) {
        final String stringUuid = UUID.randomUUID().toString();
        Player player = Player.builder()
                .state(Player.State.IDLE)
                .onHand(new ArrayList<>())
                .uuid(stringUuid)
                .name(name)
                .build();
        game.playerJoin(player);
        return Pair.of("uuid", stringUuid);
    }

    public PlayerStateDto getPlayerState(final String uuid) {
        log.info("uuid: {}", uuid);
        final Player player = game.getPlayerList().stream()
                .filter(player1 -> player1.getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new PlayerException("user with uuid: " + uuid + " not found"));
        return PlayerStateDto.builder()
                .playerName(player.getName())
                .playerState(player.getState() == null ? "null state" : player.getState().name())
                .cardOnHand(player.getOnHand()
                        .stream()
                        .map(cardResolver::getCardDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public void startGame() {
      game.startGame();
    }

    public void restartGame(){
        game.restartGame();
    }

    public GameStateDto getGameStateDto() {
        return GameStateDto.builder()
                .gameState(game.getGameState() == null ? "null state" : game.getGameState().name())
                .playerList(game.getPlayerList().stream().map(player -> PlayerDto.builder()
                        .state(player.getState())
                        .movementsBlocked(player.getMovementsBlocked())
                        .onHand(player.getOnHand())
                        .name(player.getName())
                        .build()).collect(Collectors.toList()))
                .lastOnStack(cardResolver.getCardDto(game.getCardDeck().getLastOnStack()))
                .build();
    }

}
