package pl.arturzaczek.makaoweb.rest.dto;

import lombok.*;
import pl.arturzaczek.makaoweb.game.cards.BaseCard;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerStateDto {
    private String playerName;
    private String playerState;
    private List<CardDto> cardOnHand = new ArrayList<>();
    private List<BaseCard> requestedCardsInNextMove = new ArrayList<>();
}
