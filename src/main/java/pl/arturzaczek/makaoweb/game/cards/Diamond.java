package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Data
public class Diamond extends BaseCard {
    public Diamond(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "cards.Diamond{" + super.getValue() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diamond card = (Diamond) o;
        return Objects.equals(value, card.value);
    }
}
