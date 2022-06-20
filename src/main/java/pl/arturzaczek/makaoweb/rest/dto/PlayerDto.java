package pl.arturzaczek.makaoweb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.arturzaczek.makaoweb.game.player.Player;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDto {
    private String name;
    private List<CardDto> onHand = new ArrayList<>();
    private Player.State state;
    private int movementsBlocked;
    private String uuid;
}
