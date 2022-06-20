package pl.arturzaczek.makaoweb.game.cards;

import lombok.Data;
import lombok.experimental.SuperBuilder;

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
}
