package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import br.mackenzie.Main;
import br.mackenzie.entities.Player;

public class GameScreen extends ScreenAdapter {

    private final Main game;
    private final SpriteBatch batch;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture bg;
    private float bg1x = 0f;
    private float bg2x = 0f;
    private float bg1speed = 80f;
    private float bg2speed = 160f;

    private Player player;
    private float playerX, playerY;

    private float impulso = 0f;
    private final float IMPULSO_DUR = 0.25f;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    public void show() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);

        // garanta que a câmera comece centralizada no mundo
        viewport.apply(true);
        camera.position.set(1280/2f, 720/2f, 0);
        camera.update();

        bg = new Texture(Gdx.files.internal("background.jpg"));
        player = new Player(0, 0);

        // posição horizontal: centro; vertical: chão
        float groundY = 32f; // ajuste fino da “linha do chão”
        playerX = 1280/2f - player.getWidth()/2f;
        playerY = groundY;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            impulso = IMPULSO_DUR;
        }

        boolean pedalando = impulso > 0f;
        if (pedalando) {
            impulso -= delta;
            if (impulso < 0f) impulso = 0f;
            bg1x -= bg1speed * delta;
            bg2x -= bg2speed * delta;
        }

        if (bg1x <= -1280f) bg1x += 1280f;
        if (bg2x <= -1280f) bg2x += 1280f;

        player.animateOnly(delta, pedalando);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bg, bg1x, 0, 1280, 720);
        batch.draw(bg, bg1x + 1280f, 0, 1280, 720);
        batch.draw(bg, bg2x, 0, 1280, 720);
        batch.draw(bg, bg2x + 1280f, 0, 1280, 720);
        player.drawAt(batch, playerX, playerY);
        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override
    public void dispose() {
        bg.dispose();
        player.dispose();
    }
}
