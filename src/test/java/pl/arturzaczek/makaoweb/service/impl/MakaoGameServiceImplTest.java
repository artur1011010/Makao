package pl.arturzaczek.makaoweb.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.arturzaczek.makaoweb.game.Game;
import pl.arturzaczek.makaoweb.game.cards.Diamond;
import pl.arturzaczek.makaoweb.game.cards.Heart;
import pl.arturzaczek.makaoweb.game.cards.Spade;
import pl.arturzaczek.makaoweb.game.player.Player;
import pl.arturzaczek.makaoweb.service.MakaoGameService;
import pl.arturzaczek.makaoweb.utils.CardHelper;
import pl.arturzaczek.makaoweb.utils.CardResolver;
import pl.arturzaczek.makaoweb.utils.CardValues;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class MakaoGameServiceImplTest {

    @Autowired
    CardResolver cardResolver;

    private MakaoGameService service;

    @BeforeEach
    public void setup() {
        final Player player1 = Player.builder()
                .name("player1")
                .uuid("test1")
                .state(Player.State.ACTIVE)
                .onHand(List.of(CardHelper.S2.getCard(),
                        new Diamond(CardValues._5.getValue()),
                        new Heart(CardValues._5.getValue()),
                        new Spade(CardValues._Jack.getValue()),
                        new Spade(CardValues._6.getValue())))
                .build();

        final Player player2 = Player.builder()
                .name("player2")
                .uuid("test2")
                .state(Player.State.WAITING)
                .onHand(List.of(new Diamond(CardValues._10.getValue()), new Spade(CardValues._6.getValue()), new Spade(CardValues._7.getValue()), new Spade(CardValues._9.getValue())))
                .build();

        final Player player3 = Player.builder()
                .name("player3")
                .uuid("test3")
                .state(Player.State.WAITING)
                .onHand(List.of(new Heart(CardValues._10.getValue()), new Spade(CardValues._10.getValue()), new Spade(CardValues._5.getValue()), new Heart(CardValues._9.getValue())))
                .build();

        Game game = new Game();
        game.setPlayerList(new ArrayList<>(List.of(player1, player2, player3)));
        game.setGameState(Game.GameState.PLAYING);
        service = new MakaoGameServiceImpl(game, cardResolver);
    }
}