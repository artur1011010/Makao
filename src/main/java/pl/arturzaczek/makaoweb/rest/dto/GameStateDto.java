package pl.arturzaczek.makaoweb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameStateDto {
    private List<PlayerDto> playerList;
    private CardDto lastOnStack;
    private String gameState;
    private String activePlayerUuid;
}
