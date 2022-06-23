package pl.arturzaczek.makaoweb.game.cards;

import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
public abstract class BaseCard {
    protected final String value;
    public static final String VALUE_2 = "2";
    public static final String VALUE_3 = "3";
    public static final String VALUE_4 = "4";

    public BaseCard(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isFunctionalCard(){
        return value.equals(VALUE_2) || value.equals(VALUE_3) || value.equals(VALUE_4);
    }

    public boolean is2(){
        return value.equals(VALUE_2);
    }

    public boolean is3(){
        return value.equals(VALUE_3);
    }

    public boolean is4(){
        return value.equals(VALUE_4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
