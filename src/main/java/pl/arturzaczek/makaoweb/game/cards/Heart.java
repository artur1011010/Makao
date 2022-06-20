package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

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
}
