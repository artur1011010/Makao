package pl.arturzaczek.makaoweb.game.player;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import pl.arturzaczek.makaoweb.game.cards.BaseCard;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class Player {
    private String name;
    private List<BaseCard> onHand = new ArrayList<>();
    private State state = State.IDLE;
    private int movementsBlocked;
    private List<BaseCard> requestedCardsInNextMove = new ArrayList<>();
    private int cardsToTake;
    private String uuid;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Player player = (Player) o;
        return Objects.equals(uuid, player.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public void removeCardsFromHand(List<BaseCard> cardsToRemove) {
        cardsToRemove.forEach(card -> onHand.remove(card));
    }

    public int getMovementsBlockedAndDecrement() {
        if (movementsBlocked > 0) {
            if (movementsBlocked == 1) {
                state = State.WAITING;
            }
            final int copy = movementsBlocked;
            movementsBlocked--;
            return copy;
        } else {
            return 0;
        }
    }

    public void setBlockMovements(final int rounds) {
        if (movementsBlocked != 0 || state == State.BLOCKED) {
            log.error("gracz jest zablokowany, nie moza go blokowac skoro nie uczestniczy w grze");
        } else {
            movementsBlocked = rounds;
            state = State.BLOCKED;
        }
    }

    public boolean has2OnHand() {
        return onHand.stream()
                .anyMatch(card -> card.getValue().equals(BaseCard.VALUE_2));
    }

    public boolean has3OnHand() {
        return onHand.stream()
                .anyMatch(card -> card.getValue().equals(BaseCard.VALUE_3));
    }

    public boolean has4OnHand() {
        return onHand.stream()
                .anyMatch(card -> card.getValue().equals(BaseCard.VALUE_4));
    }

    @AllArgsConstructor
    @Getter
    public enum State {
        IDLE("bezczynny - nie uczestniczy w grze"),
        ACTIVE("wykonuje ruch"),
        BLOCKED("zablokowany na ilosc ruchow"),
        WAITING("czeka na ruch"),
        FINISHED("zakonczył gre");
        private final String description;
    }
}
