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
import com.badlogic.gdx.audio.Sound;
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

    // estatísticas globais
    private float pedaladasPorSegundoMaxima = 0f;
    private float somaCadencias = 0f;
    private int contagemCadencias = 0;

    // som de fundo
    private Music backgroundMusic;

    // SISTEMA DE BEEP DE ALERTA
    private Sound warningBeep;
    private float beepCooldown = 0f;
    private final float BEEP_INTERVAL = 1.5f;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();

        // === CÂMERA + VIEWPORT (inicializa UMA vez) ===
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply(true);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        camera.update();

        // === CARREGA BACKGROUNDS (UMA VEZ) ===
        bgDay = new Texture(Gdx.files.internal("background_day.jpg"));
        bgAfternoon = new Texture(Gdx.files.internal("background_afternoon.jpg"));
        bgNight = new Texture(Gdx.files.internal("background_night.jpg"));
        currentBg = bgDay;

        // === PLAYER E POSIÇÃO BASE ===
        float groundY = 32f;
        player = new Player(0, 0);
        playerX = 1280 / 2f - player.getWidth() / 2f;
        playerY = groundY;

        // === SISTEMAS ===
        pedalController = new PedalController();
        hud = new Hud();
        enemyManager = new EnemyManager(groundY);
        phaseManager = new PhaseManager();

        // texto inicial de nível
        showLevelText(1);

        // zera estatísticas globais
        tempoDecorrido = 0f;
        pontos = 0f;
        pedaladasPorSegundoMaxima = 0f;
        somaCadencias = 0f;
        contagemCadencias = 0;

        // === MÚSICA ===
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundSound.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.45f);

        // === SOM DE ALERTA ===
        warningBeep = Gdx.audio.newSound(Gdx.files.internal("sounds/bip_down.mp3"));
    }

    @Override
    public void show() {
        // quando essa tela volta (ex: depois do pause), NÃO recriamos nada
        // só garantimos input e retomamos a música
        Gdx.input.setInputProcessor(null);
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    private void showLevelText(int level) {
        levelText = "NÍVEL " + level;
        levelTextTimer = LEVEL_TEXT_DURATION;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // === PAUSE (ESC) ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (backgroundMusic != null) backgroundMusic.pause();
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        // === UPDATE ===
        tempoDecorrido += delta;

        pedalController.update(delta);
        boolean pedaling = pedalController.isPedaling();
        float pps = pedalController.getPedaladasPorSegundo();

        // sistema de fases
        boolean mudouFase = phaseManager.update(delta, pps);
        if (mudouFase) {
            int novaFase = phaseManager.getFaseAtual();

            // muda fundo
            if (novaFase == 2) {
                startBackgroundTransition(bgAfternoon);
            } else if (novaFase == 3) {
                startBackgroundTransition(bgNight);
            }

            // aumenta dificuldade do inimigo
            enemyManager.setNivel(novaFase);

            // texto de nível
            showLevelText(novaFase);

            // vitória (todas as fases concluídas)
            if (phaseManager.isUltimaFase()) {
                vitoria();
                return;
            }
        }

        // estatísticas globais
        if (pps > pedaladasPorSegundoMaxima) {
            pedaladasPorSegundoMaxima = pps;
        }
        somaCadencias += pps;
        contagemCadencias++;

        // fundo parallax
        updateBackground(delta, pedaling, pps);

        // pontuação
        pontos += pps * delta * 10f;

        // anima somente (posição do player é fixa)
        player.animateOnly(delta, pedaling);

        // velocidade do inimigo baseada na cadência
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

        // === SISTEMA DE BEEP DE ALERTA ===
        boolean emPerigo = phaseManager.isEmRisco(pps);

        if (emPerigo) {
            beepCooldown -= delta;
            if (beepCooldown <= 0f) {
                warningBeep.play(0.4f);
                beepCooldown = BEEP_INTERVAL;
            }
        } else {
            beepCooldown = 0f;
        }

        // === DRAW ===
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground(batch);

        enemyManager.render(batch, playerY);
        player.drawAt(batch, playerX, playerY);

        float distanciaInimigo = enemyManager.getDistanciaAteJogador(playerX);
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
        if (backgroundMusic != null) backgroundMusic.stop();

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
        if (backgroundMusic != null) backgroundMusic.stop();

        float cadenciaMedia = phaseManager.getCadenciaMedia();

        GameStats stats = new GameStats(
            tempoDecorrido,
            pedalController.getTotalPedaladas(),
            pontos,
            phaseManager.getNivelCompletado(),
            pedaladasPorSegundoMaxima,
            cadenciaMedia
        );

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

        if (warningBeep != null)
            warningBeep.dispose();

        if (bgDay != null) bgDay.dispose();
        if (bgAfternoon != null) bgAfternoon.dispose();
        if (bgNight != null) bgNight.dispose();

        player.dispose();
        hud.dispose();
        enemyManager.dispose();
    }
}
