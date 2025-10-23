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

/**
 * GameScreen simples:
 * - Mostra fundo e personagem (player)
 * - Move o player com as setas
 * - ESC volta para o menu
 */
public class GameScreen extends ScreenAdapter {

    private final Main game;
    private final SpriteBatch batch;

    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture bgTexture;
    private Texture playerTexture;

    // posição do jogador
    private float playerX;
    private float playerY;
    private float playerSpeed = 200f; // pixels por segundo

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

        // coloca o player no centro da tela
        playerX = (1280 / 2f) - (playerTexture.getWidth() / 2f);
        playerY = (720 / 2f) - (playerTexture.getHeight() / 2f);
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

        // desenhar fundo e player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bgTexture, 0, 0, 1280, 720); // fundo cobrindo a tela
        batch.draw(playerTexture, playerX, playerY);
        batch.end();

        // tecla ESC volta para o menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    private void handleInput(float delta) {
        // movimento com setas
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerX -= playerSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerX += playerSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerY += playerSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerY -= playerSpeed * delta;
        }

        // limites da tela (pra não sair)
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
