package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Data
public class Heart extends BaseCard{
    public Heart(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "cards.Heart{" + super.getValue() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Heart card = (Heart) o;
        return Objects.equals(value, card.value);
    }
}
