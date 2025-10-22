package br.mackenzie;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import br.mackenzie.screens.MenuScreen;

public class Main extends Game {
    private SpriteBatch batch;

    public SpriteBatch getBatch() { return batch; }

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MenuScreen(this));
    }

    /** Chamado pelo menu ao clicar em "Iniciar". */
    public void goToGame() {
        // TODO: quando tiver GameScreen, troque aqui:
        // setScreen(new GameScreen(this));
        System.out.println("Iniciar Sessão clicado — implemente goToGame() com sua GameScreen.");
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        super.dispose();
    }
}
