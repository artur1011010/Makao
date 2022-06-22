package pl.arturzaczek.makaoweb.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.arturzaczek.makaoweb.rest.dto.GameStateDto;
import pl.arturzaczek.makaoweb.rest.dto.MoveDto;
import pl.arturzaczek.makaoweb.rest.dto.PlayerDto;
import pl.arturzaczek.makaoweb.service.GameService;

@RequestMapping("/api")
@RestController
@Slf4j
@RequiredArgsConstructor
public class GameController {

    private final GameService service;

    @PostMapping("/player")
    public ResponseEntity<Pair<String, String>> createPlayerAndJoinGame(@RequestParam final String name) {
        log.info("POST  api/player?name={}", name);
        return ResponseEntity.ok(service.createPlayerAndJoinGame(name));
    }

    @GetMapping("/player/state")
    public ResponseEntity<PlayerDto> getPlayerState(@RequestHeader final String uuid) {
        log.info("GET  api/player/state");
        return ResponseEntity.ok(service.getPlayerState(uuid));
    }

    @GetMapping("/game/state")
    public ResponseEntity<GameStateDto> getGameState() {
        log.info("GET  api/game/state");
        return ResponseEntity.ok(service.getGameStateDto());
    }

    @PatchMapping("/game/start")
    public ResponseEntity<Void> startGame() {
        log.info("PATCH  api/game/start");
        service.startGame();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/game/restart")
    public ResponseEntity<Void> restartGame() {
        log.info("PATCH  api/game/restart");
        service.restartGame();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/game/move")
    public ResponseEntity<Void> move(@RequestHeader final String uuid, @RequestBody final MoveDto moveDto) {
        log.info("POST  api/game/move");
        service.move(uuid, moveDto);
        return ResponseEntity.ok().build();
    }
}
