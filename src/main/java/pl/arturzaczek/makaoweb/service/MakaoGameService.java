package pl.arturzaczek.makaoweb.service;

import org.apache.commons.lang3.tuple.Pair;
import pl.arturzaczek.makaoweb.rest.dto.GameStateDto;
import pl.arturzaczek.makaoweb.rest.dto.MoveDto;
import pl.arturzaczek.makaoweb.rest.dto.PlayerDto;


public interface MakaoGameService {

    Pair<String, String> createPlayerAndJoinGame(String name);

    PlayerDto getPlayerState(String uuid);

    void startGame();

    void restartGame();

    GameStateDto getGameStateDto();

    void move(String uuid, MoveDto moveDto);
}
