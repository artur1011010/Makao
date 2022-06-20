package pl.arturzaczek.makaoweb.rest.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerStateDto {
    String playerName;
    String playerState;
    List<CardDto> cardOnHand = new ArrayList<>();
}
