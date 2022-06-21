package pl.arturzaczek.makaoweb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pl.arturzaczek.makaoweb.game.Game;
import pl.arturzaczek.makaoweb.game.exception.PlayerException;
import pl.arturzaczek.makaoweb.game.player.Player;
import pl.arturzaczek.makaoweb.rest.dto.GameStateDto;
import pl.arturzaczek.makaoweb.rest.dto.PlayerDto;
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

    public PlayerDto getPlayerState(final String uuid) {
        log.info("uuid: {}", uuid);
        final Player player = getPlayerByUuid(uuid);
        return PlayerDto.builder()
                .state(player.getState() == null ? Player.State.IDLE : player.getState())
                .movementsBlocked(player.getMovementsBlocked())
                .onHand(player.getOnHand()
                        .stream()
                        .map(cardResolver::getCardDto)
                        .collect(Collectors.toList()))
                .name(player.getName())
                .build();
    }

    private Player getPlayerByUuid(final String uuid) {
        return game.getPlayerList().stream()
                .filter(player1 -> player1.getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new PlayerException("user with uuid: " + uuid + " not found"));
    }

    public void startGame() {
        game.startGame();
    }

    public void restartGame() {
        game.restartGame();
    }

    public GameStateDto getGameStateDto() {
        return GameStateDto.builder()
                .gameState(game.getGameState() == null ? "null state" : game.getGameState().name())
                .playerList(game.getPlayerList().stream().map(player -> PlayerDto.builder()
                        .state(player.getState())
                        .movementsBlocked(player.getMovementsBlocked())
                        .onHand(player.getOnHand()
                                .stream()
                                .map(cardResolver::getCardDto)
                                .collect(Collectors.toList()))
                        .name(player.getName())
                        .build()).collect(Collectors.toList()))
                .lastOnStack(cardResolver.getCardDto(game.getCardDeck().getLastOnStack()))
                .build();
    }

}
