package pl.arturzaczek.makaoweb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pl.arturzaczek.makaoweb.game.Game;
import pl.arturzaczek.makaoweb.game.cards.BaseCard;
import pl.arturzaczek.makaoweb.game.player.Player;
import pl.arturzaczek.makaoweb.rest.dto.GameStateDto;
import pl.arturzaczek.makaoweb.rest.dto.MoveDto;
import pl.arturzaczek.makaoweb.rest.dto.PlayerDto;
import pl.arturzaczek.makaoweb.utils.CardHelper;
import pl.arturzaczek.makaoweb.service.GameService;
import pl.arturzaczek.makaoweb.utils.CardResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final Game game;
    private final CardResolver cardResolver;

    @Override
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

    @Override
    public PlayerDto getPlayerState(final String uuid) {
        log.info("uuid: {}", uuid);
        final Player player = game.getPlayerByUuid(uuid);
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

    @Override
    public void startGame() {
        game.startGame();
    }

    @Override
    public void restartGame() {
        game.restartGame();
    }

    @Override
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

    @Override
    public void move(final String uuid, final MoveDto moveDto) {
        log.info("\nuuid {}\nmoveDto {}", uuid, moveDto);
        final Player playerByUuid = game.getPlayerByUuid(uuid);
        if (CollectionUtils.isEmpty(moveDto.getPutAside())) {
            playerByUuid.getOnHand().add(game.getCardDeck().getNextCard());
            log.info("nie odłozono kart - dobierasz");
        } else {
            log.info("odłozono karty");
            final List<BaseCard> baseCards = moveDto.getPutAside()
                    .stream()
                    .map(cardResolver::getBaseCard)
                    .collect(Collectors.toList());

            game.getCardDeck().setLastOnStack(baseCards.get(baseCards.size() - 1));
            log.info("ustawiono ostatnia karte na {}", baseCards.get(0));
            playerByUuid.removeCardsFromHand(baseCards);
        }
    }

    private boolean checkIfCardsAreFunctional(final List<BaseCard> baseCards) {
        return baseCards.stream()
                .anyMatch(BaseCard::isFunctionalCard);
    }


    private void actionCard2(final Player player) {
        final Player nextPlayer = game.getNextPlayerByCurrentUuid(player.getUuid());
        if (nextPlayer.has2OnHand()) {
            nextPlayer.setRequestedCardsInNextMove(CardHelper.ALL2.getCards());
        } else {

        }
    }

    private void actionCard3(final Player player) {
        final Player nextPlayer = game.getNextPlayerByCurrentUuid(player.getUuid());
        if (nextPlayer.has3OnHand()) {
            nextPlayer.setRequestedCardsInNextMove(CardHelper.ALL3.getCards());
        }
    }

    private void actionCard4(final Player player) {
        final Player nextPlayer = game.getNextPlayerByCurrentUuid(player.getUuid());
        if (nextPlayer.has4OnHand()) {
            nextPlayer.setRequestedCardsInNextMove(CardHelper.ALL4.getCards());
        }
    }
}
