package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

    // --- BACKGROUNDS: dia → tarde → noite com transição fade ---
    private Texture bgDay, bgAfternoon, bgNight;
    private Texture currentBg, nextBg;

    private float bg1x = 0f;
    private float bg2x = 0f;
    private float bg1speedBase = 80f;
    private float bg2speedBase = 160f;

    private boolean transitioning = false;
    private float transitionAlpha = 0f;
    private float transitionDuration = 2f; // segundos
    private float transitionTimer = 0f;

    // 1=dia, 2=tarde, 3=noite
    private int currentLevel = 1;

    private Player player;
    private float playerX, playerY;

    private float impulso = 0f;
    private final float IMPULSO_DUR = 0.25f;

    // HUD
    private float tempoDecorrido = 0f;
    private float pedaladasPorSegundo = 0f;
    private float pontos = 0f;
    private int totalPedaladas = 0;

    private float janelaTempo = 1f; // 1 segundo
    private float tempoDesdeUltimoReset = 0f;
    private int pedaladasRecentes = 0;

    private BitmapFont font;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);

        viewport.apply(true);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        camera.update();

        // Carrega fundos
        bgDay = new Texture(Gdx.files.internal("background_day.jpg"));
        bgAfternoon = new Texture(Gdx.files.internal("background_afternoon.jpg"));
        bgNight = new Texture(Gdx.files.internal("background_night.jpg"));
        currentBg = bgDay; // começa de dia

        player = new Player(0, 0);

        float groundY = 32f;
        playerX = 1280 / 2f - player.getWidth() / 2f;
        playerY = groundY;

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
    }

    private void startBackgroundTransition(Texture newBg) {
        if (newBg == null || newBg == currentBg) return;
        nextBg = newBg;
        transitioning = true;
        transitionTimer = 0f;
        transitionAlpha = 0f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tempoDecorrido += delta;
        tempoDesdeUltimoReset += delta;

        // Detecta pedalada (barra de espaço)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            impulso = IMPULSO_DUR;
            totalPedaladas++;
            pedaladasRecentes++;
        }

        boolean pedalando = impulso > 0f;
        if (pedalando) {
            impulso -= delta;
            if (impulso < 0f) impulso = 0f;

            // fundo mexe mais rápido se pedalar mais rápido
            float speedMultiplier = 1f + pedaladasPorSegundo / 3f;
            float bg1speed = bg1speedBase * speedMultiplier;
            float bg2speed = bg2speedBase * speedMultiplier;

            // o fundo se move só quando pedala; sem pedalar = parado
            bg1x -= bg1speed * delta;
            bg2x -= bg2speed * delta;
        }

        // Atualiza pedaladas por segundo a cada segundo
        if (tempoDesdeUltimoReset >= janelaTempo) {
            pedaladasPorSegundo = pedaladasRecentes / tempoDesdeUltimoReset;
            pedaladasRecentes = 0;
            tempoDesdeUltimoReset = 0f;
        }

        // Pontos cumulativos baseados em pedaladas por segundo
        pontos += pedaladasPorSegundo * delta * 10f;

        // Loop do fundo
        if (bg1x <= -1280f) bg1x += 1280f;
        if (bg2x <= -1280f) bg2x += 1280f;

        // Troca de período por tempo (dia 60s → tarde, 120s → noite)
        if (!transitioning) {
            if (tempoDecorrido > 5f && currentLevel == 1) {
                startBackgroundTransition(bgAfternoon);
                currentLevel = 2;
            } else if (tempoDecorrido > 15f && currentLevel == 2) {
                startBackgroundTransition(bgNight);
                currentLevel = 3;
            }
        }

        // Atualiza fade de transição
        if (transitioning) {
            transitionTimer += delta;
            transitionAlpha = Math.min(1f, transitionTimer / transitionDuration);
            if (transitionAlpha >= 1f) {
                currentBg = nextBg;
                nextBg = null;
                transitioning = false;
                transitionAlpha = 0f;
            }
        }

        player.animateOnly(delta, pedalando);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // --- Fundo base (current) ---
        batch.draw(currentBg, bg1x, 0, 1280, 720);
        batch.draw(currentBg, bg1x + 1280f, 0, 1280, 720);
        batch.draw(currentBg, bg2x, 0, 1280, 720);
        batch.draw(currentBg, bg2x + 1280f, 0, 1280, 720);

        // --- Fundo em transição
        if (transitioning && nextBg != null) {
            batch.setColor(1f, 1f, 1f, transitionAlpha);
            batch.draw(nextBg, bg1x, 0, 1280, 720);
            batch.draw(nextBg, bg1x + 1280f, 0, 1280, 720);
            batch.draw(nextBg, bg2x, 0, 1280, 720);
            batch.draw(nextBg, bg2x + 1280f, 0, 1280, 720);
            batch.setColor(Color.WHITE); // reset obrigatório
        }

        // Jogador
        player.drawAt(batch, playerX, playerY);

        // HUD
        font.draw(batch, String.format("Tempo: %.1fs", tempoDecorrido), 40, 700);
        font.draw(batch, String.format("Pedaladas por segundo: %.1f", pedaladasPorSegundo), 40, 660);
        font.draw(batch, String.format("Pontos: %.0f", pontos), 40, 620);
        font.draw(batch, String.format("Pedaladas totais: %d", totalPedaladas), 40, 580);

        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override
    public void dispose() {
        // Descarta somente as texturas efetivamente carregadas aqui
        if (bgDay != null) bgDay.dispose();
        if (bgAfternoon != null) bgAfternoon.dispose();
        if (bgNight != null) bgNight.dispose();

        player.dispose();
        font.dispose();
    }
}

