package pl.arturzaczek.makaoweb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.arturzaczek.makaoweb.game.cards.BaseCard;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameStateDto {
    private CardDto lastOnStack;
    private String gameState;
    private String activePlayerUuid;
    private List<PlayerDto> playerList = new ArrayList<>();
    private List<BaseCard> requestedCardsInNextMove = new ArrayList<>();
}
