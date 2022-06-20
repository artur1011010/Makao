package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

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
}
