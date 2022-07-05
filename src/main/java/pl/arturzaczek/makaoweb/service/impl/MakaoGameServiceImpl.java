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
import pl.arturzaczek.makaoweb.service.MakaoGameService;
import pl.arturzaczek.makaoweb.utils.CardResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MakaoGameServiceImpl implements MakaoGameService {

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
                .name(player.getName())
                .onHand(player.getOnHand()
                        .stream()
                        .map(cardResolver::getCardDto)
                        .collect(Collectors.toList()))
                .movementsBlocked(player.getMovementsBlocked())
                .requestedCardsInNextMove(player.getRequestedCardsInNextMove() != null ? player.getRequestedCardsInNextMove()
                        .stream()
                        .map(cardResolver::getCardDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
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
                .activePlayerUuid(game.getActivePlayerUuid())
                .build();
    }

    @Override
    public void move(final String uuid, final MoveDto moveDto) {
        log.info("\nuuid {}\nmoveDto {}", uuid, moveDto);
        final Player currentPlayer = game.getPlayerByUuid(uuid);
        clearPlayerAfterMove(currentPlayer);
        final Player nextPlayer = game.getNextWaitingPlayerAndSetActiveByCurrentUuid(currentPlayer.getUuid());
        if (CollectionUtils.isEmpty(moveDto.getPutAside())) {
            game.addCardsToPlayerHand(1, currentPlayer);
        } else {
            final List<BaseCard> baseCards = moveDto.getPutAside()
                    .stream()
                    .map(cardResolver::getBaseCard)
                    .collect(Collectors.toList());
            game.getCardDeck().setLastOnStack(baseCards.get(baseCards.size() - 1));
            currentPlayer.removeCardsFromHand(baseCards);
            if (checkIfCardsAreFunctional(baseCards)) {
                if (baseCards.stream().anyMatch(BaseCard::is2)) {
                    actionCard2(nextPlayer, baseCards.size());
                } else if (baseCards.stream().anyMatch(BaseCard::is3)) {
                    actionCard3(nextPlayer, baseCards.size());
                } else if (baseCards.stream().anyMatch(BaseCard::is4)) {
                    actionCard4(nextPlayer, baseCards.size());
                }
            }
        }
        checkIfAnyPlayerWonAndRestartGame();
    }

    private boolean checkIfCardsAreFunctional(final List<BaseCard> baseCards) {
        return baseCards.stream()
                .anyMatch(BaseCard::isFunctionalCard);
    }

    private void actionCard2(final Player nextPlayer, final int functionalCardsOnStack) {
        if (nextPlayer.has2OnHand()) {
            game.setFunctionalCards(game.getFunctionalCards() + functionalCardsOnStack);
            nextPlayer.setRequestedCardsInNextMove(CardHelper.ALL2.getCards());
        } else {
            final int amount = 2 * (functionalCardsOnStack + game.getFunctionalCards());
            game.addCardsToPlayerHand(amount, nextPlayer);
            game.setFunctionalCards(0);
        }
    }

    private void actionCard3(final Player nextPlayer, final int functionalCardsOnStack) {
        if (nextPlayer.has3OnHand()) {
            game.setFunctionalCards(game.getFunctionalCards() + functionalCardsOnStack);
            nextPlayer.setRequestedCardsInNextMove(CardHelper.ALL3.getCards());
        } else {
            final int amount = 3 * (functionalCardsOnStack + game.getFunctionalCards());
            game.addCardsToPlayerHand(amount, nextPlayer);
            game.setFunctionalCards(0);
        }
    }

    private void actionCard4(final Player nextPlayer, final int functionalCardsOnStack) {
        if (nextPlayer.has4OnHand()) {
            game.setFunctionalCards(game.getFunctionalCards() + functionalCardsOnStack);
            nextPlayer.setRequestedCardsInNextMove(CardHelper.ALL4.getCards());
        } else {
            //todo umozliwia wykonanie nastepnej kolejki - trzeba zastosować od razu
            nextPlayer.setMovementsBlocked(functionalCardsOnStack + game.getFunctionalCards());
            nextPlayer.setState(Player.State.BLOCKED);
            game.getNextWaitingPlayerAndSetActiveByCurrentUuid(nextPlayer.getUuid());
            game.setFunctionalCards(0);
        }
    }

    private void checkIfAnyPlayerWonAndRestartGame() {
        Player winnerCandidate = game.getWinner();
        if (winnerCandidate != null) {
            game.setGameStateFinishedAndClearTable();
            winnerCandidate.setState(Player.State.FINISHED);
        }
    }

    private void clearPlayerAfterMove(final Player player) {
        player.setState(Player.State.WAITING);
        player.setRequestedCardsInNextMove(new ArrayList<>());
    }
}