package pl.arturzaczek.makaoweb.game.cards;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class BaseCard {
    protected String value;

    public BaseCard(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
