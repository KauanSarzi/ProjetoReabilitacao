package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import br.mackenzie.Main;

public class GameScreen extends ScreenAdapter {

    private final Main game;
    private final SpriteBatch batch;

    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture bgTexture;
    private Texture playerTexture;

    // posições
    private float playerX;
    private float playerY;
    private float bgX; // posição horizontal do fundo
    private float playerSpeed = 200f; // pixels por segundo
    private float bgSpeed = 150f;     // velocidade do fundo

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        stage = new Stage(viewport, batch);

        loadTextures();

        // posiciona player no centro
        playerX = (1280 / 2f) - (playerTexture.getWidth() / 2f);
        playerY = (720 / 2f) - (playerTexture.getHeight() / 2f);

        bgX = 0;
    }

    private void loadTextures() {
        bgTexture = new Texture(Gdx.files.internal("background.png"));
        playerTexture = new Texture(Gdx.files.internal("player.png"));
    }

    @Override
    public void render(float delta) {
        // limpar tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // entrada do jogador
        handleInput(delta);

        // desenha o fundo e player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        //desenha 2x para loop contínuo
        batch.draw(bgTexture, bgX, 0, 1280, 720);
        batch.draw(bgTexture, bgX + 1280, 0, 1280, 720);
        batch.draw(playerTexture, playerX, playerY);
        batch.end();

        if (bgX <= -1280) {
            bgX = 0;
        }

        //esc volta para o menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    private void handleInput(float delta) {

        // mover fundo a cada click do espaco
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            bgX -= bgSpeed * delta;
        }

        if (playerX < 0) playerX = 0;
        if (playerY < 0) playerY = 0;
        if (playerX > 1280 - playerTexture.getWidth()) playerX = 1280 - playerTexture.getWidth();
        if (playerY > 720 - playerTexture.getHeight()) playerY = 720 - playerTexture.getHeight();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
        if (playerTexture != null) playerTexture.dispose();
        stage.dispose();
    }
}
