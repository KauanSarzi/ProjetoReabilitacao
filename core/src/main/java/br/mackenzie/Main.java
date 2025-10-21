package br.mackenzie;
import br.mackenzie.screens.GameScreen;

import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        // Define a tela inicial do jogo
        setScreen(new GameScreen());
    }
}
