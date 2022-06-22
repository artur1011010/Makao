package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Data
public class Spade extends BaseCard{
    public Spade(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "cards.Spade{" + super.getValue() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spade card = (Spade) o;
        return Objects.equals(value, card.value);
    }
}
