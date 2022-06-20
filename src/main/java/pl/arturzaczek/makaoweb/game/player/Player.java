package pl.arturzaczek.makaoweb.game.player;

import lombok.*;
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
public class Player {
    private String name;
    private List<BaseCard> onHand = new ArrayList<>();
    private State state = State.IDLE;
    private int movementsBlocked;
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

    @AllArgsConstructor
    @Getter
    public enum State{
        IDLE("bezczynny - nie uczestniczy w grze"),
        ACTIVE("wykonuje ruch"),
        BLOCKED("zablokowany na ilosc ruchow"),
        WAITING("czeka na ruch"),
        FINISHED("zakonczył gre");
        private final String description;
    }
}
