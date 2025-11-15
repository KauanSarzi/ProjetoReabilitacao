
package br.mackenzie.screens;

import br.mackenzie.data.GameStats;
import br.mackenzie.input.PedalController;
import br.mackenzie.logic.EnemyManager;
import br.mackenzie.logic.PhaseManager;
import br.mackenzie.ui.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
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

    // BACKGROUND
    private Texture bgDay, bgAfternoon, bgNight;
    private Texture currentBg, nextBg;
    private float bg1x = 0f;
    private float bg2x = 0f;
    private float bg1speedBase = 60f;
    private float bg2speedBase = 120f;
    private boolean transitioning = false;
    private float transitionAlpha = 0f;
    private float transitionDuration = 2f;
    private float transitionTimer = 0f;

    private Player player;
    private float playerX, playerY;

    // sistemas
    private PedalController pedalController;
    private Hud hud;
    private EnemyManager enemyManager;
    private PhaseManager phaseManager;

    // lógica de jogo
    private float tempoDecorrido = 0f;
    private float pontos = 0f;
    private String levelText = "";
    private float levelTextTimer = 0f;
    private final float LEVEL_TEXT_DURATION = 2.5f;

    // estatísticas para tracking
    private float pedaladasPorSegundoMaxima = 0f;
    private float somaCadencias = 0f;
    private int contagemCadencias = 0;

    // som de fundo
    private Music backgroundMusic;

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

        // carrega fundos
        bgDay = new Texture(Gdx.files.internal("background_day.jpg"));
        bgAfternoon = new Texture(Gdx.files.internal("background_afternoon.jpg"));
        bgNight = new Texture(Gdx.files.internal("background_night.jpg"));
        currentBg = bgDay;

        // player
        player = new Player(0, 0);
        float groundY = 32f;
        playerX = 1280 / 2f - player.getWidth() / 2f;
        playerY = groundY;

        // sistemas
        pedalController = new PedalController();
        hud = new Hud();
        enemyManager = new EnemyManager(groundY);
        phaseManager = new PhaseManager();

        showLevelText(1);

        // som de fundo
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundSound.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.45f);
        backgroundMusic.play();
    }

    private void showLevelText(int level) {
        levelText = "NÍVEL " + level;
        levelTextTimer = LEVEL_TEXT_DURATION;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // PAUSE
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            backgroundMusic.pause();
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        // UPDATE
        tempoDecorrido += delta;

        pedalController.update(delta);
        boolean pedaling = pedalController.isPedaling();
        float pps = pedalController.getPedaladasPorSegundo();

        // ATUALIZA SISTEMA DE FASES
        boolean mudouFase = phaseManager.update(delta, pps);
        if (mudouFase) {
            int novaFase = phaseManager.getFaseAtual();

            // Atualiza visual (background)
            if (novaFase == 2) {
                startBackgroundTransition(bgAfternoon);
            } else if (novaFase == 3) {
                startBackgroundTransition(bgNight);
            }

            // Atualiza nível do Enemy
            enemyManager.setNivel(novaFase);

            // Mostra texto de nível
            showLevelText(novaFase);

            // Verifica vitória (completou todas as fases)
            if (phaseManager.isUltimaFase()) {
                vitoria();
                return;
            }
        }

        // statisticas
        if (pps > pedaladasPorSegundoMaxima)
            pedaladasPorSegundoMaxima = pps;

        somaCadencias += pps;
        contagemCadencias++;

        updateBackground(delta, pedaling, pps);

        pontos += pps * delta * 10f;

        player.animateOnly(delta, pedaling);

        // CALCULA MULTIPLICADOR DE VELOCIDADE DO ENEMY
        float enemySpeedMultiplier = phaseManager.getEnemySpeedMultiplier(pps);

        boolean jogadorCapturado = enemyManager.update(
            delta,
            playerX,
            playerY,
            player.getWidth(),
            player.getHeight(),
            pps,
            enemySpeedMultiplier
        );

        if (jogadorCapturado) {
            gameOver();
            return;
        }

        // DRAW
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground(batch);

        enemyManager.render(batch, playerY);
        player.drawAt(batch, playerX, playerY);

        float distanciaInimigo = enemyManager.getDistanciaAteJogador(playerX);
        boolean emPerigo = phaseManager.isEmRisco(pps);
        float velocidadeMinima = phaseManager.getCadenciaMinima();

        hud.render(
            batch,
            tempoDecorrido,
            pps,
            pontos,
            pedalController.getTotalPedaladas(),
            levelText,
            levelTextTimer,
            distanciaInimigo,
            emPerigo,
            velocidadeMinima,
            phaseManager.getDistanciaPercorrida(),
            phaseManager.getDistanciaMinima(),
            phaseManager.getProgressoDistancia(),
            phaseManager.getCadenciaMedia(),
            phaseManager.getProgressoCadencia()
        );

        batch.end();
    }

    // GAME OVER
    private void gameOver() {
        backgroundMusic.stop();

        float cadenciaMedia = contagemCadencias > 0 ?
            somaCadencias / contagemCadencias : 0f;

        GameStats stats = new GameStats(
            tempoDecorrido,
            pedalController.getTotalPedaladas(),
            pontos,
            phaseManager.getFaseAtual(),
            pedaladasPorSegundoMaxima,
            cadenciaMedia
        );

        game.setScreen(new GameOverScreen(game, stats));
    }

    // VITÓRIA
    private void vitoria() {
        backgroundMusic.stop();

        float cadenciaMedia = phaseManager.getCadenciaMedia();

        GameStats stats = new GameStats(
            tempoDecorrido,
            pedalController.getTotalPedaladas(),
            pontos,
            phaseManager.getFaseAtual(),
            pedaladasPorSegundoMaxima,
            cadenciaMedia
        );

        // TODO: Criar VictoryScreen - por enquanto usa GameOverScreen
        game.setScreen(new GameOverScreen(game, stats));
    }

    private void updateBackground(float delta, boolean moving, float pps) {
        float speedMultiplier = 1f + Math.min(pps / 6f, 20.5f);

        if (pps < 0.5f)
            speedMultiplier = 1f;

        if (moving) {
            float bg1speed = bg1speedBase * speedMultiplier;
            float bg2speed = bg2speedBase * speedMultiplier;

            bg1x -= bg1speed * delta;
            bg2x -= bg2speed * delta;
        }

        if (bg1x <= -1280f) bg1x += 1280f;
        if (bg2x <= -1280f) bg2x += 1280f;

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

        if (levelTextTimer > 0f) {
            levelTextTimer -= delta;
            if (levelTextTimer < 0f) levelTextTimer = 0f;
        }
    }

    private void renderBackground(SpriteBatch batch) {
        batch.draw(currentBg, bg1x, 0, 1280, 720);
        batch.draw(currentBg, bg1x + 1280f, 0, 1280, 720);
        batch.draw(currentBg, bg2x, 0, 1280, 720);
        batch.draw(currentBg, bg2x + 1280f, 0, 1280, 720);

        if (transitioning && nextBg != null) {
            batch.setColor(1f, 1f, 1f, transitionAlpha);
            batch.draw(nextBg, bg1x, 0, 1280, 720);
            batch.draw(nextBg, bg1x + 1280f, 0, 1280, 720);
            batch.draw(nextBg, bg2x, 0, 1280, 720);
            batch.draw(nextBg, bg2x + 1280f, 0, 1280, 720);
            batch.setColor(Color.WHITE);
        }
    }

    private void startBackgroundTransition(Texture newBg) {
        if (newBg == null || newBg == currentBg) return;
        nextBg = newBg;
        transitioning = true;
        transitionTimer = 0f;
        transitionAlpha = 0f;
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override
    public void dispose() {
        if (backgroundMusic != null)
            backgroundMusic.dispose();

        if (bgDay != null) bgDay.dispose();
        if (bgAfternoon != null) bgAfternoon.dispose();
        if (bgNight != null) bgNight.dispose();

        player.dispose();
        hud.dispose();
        enemyManager.dispose();
    }
}
