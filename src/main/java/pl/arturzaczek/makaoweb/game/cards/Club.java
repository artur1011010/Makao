package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Data
public class Club extends BaseCard{
    public Club(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "cards.Club{" + super.getValue() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club card = (Club) o;
        return Objects.equals(value, card.value);
    }
}
