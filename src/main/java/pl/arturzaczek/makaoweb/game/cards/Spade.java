package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

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
}
