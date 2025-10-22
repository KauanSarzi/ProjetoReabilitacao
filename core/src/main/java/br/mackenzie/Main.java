package br.mackenzie;
import br.mackenzie.screens.GameScreen;

import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
