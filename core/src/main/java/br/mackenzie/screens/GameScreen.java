package br.mackenzie.screens;

import br.mackenzie.data.GameStats;
import br.mackenzie.input.PedalController;
import br.mackenzie.logic.EnemyManager;
import br.mackenzie.ui.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
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

    // BACKGROUND (fica na própria tela)
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
    private int currentLevel = 1;

    private Player player;
    private float playerX, playerY;

    // sistemas novos
    private PedalController pedalController;
    private Hud hud;
    private EnemyManager enemyManager;

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

        player = new Player(0, 0);
        float groundY = 32f;
        playerX = 1280 / 2f - player.getWidth() / 2f;
        playerY = groundY;

        pedalController = new PedalController();
        hud = new Hud();

        // Inicializa o inimigo na mesma altura do jogador
        enemyManager = new EnemyManager(groundY);

        showLevelText(1);
    }

    private void showLevelText(int level) {
        levelText = "NÍVEL " + level;
        levelTextTimer = LEVEL_TEXT_DURATION;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        // UPDATE GERAL
        tempoDecorrido += delta;

        // atualiza pedal
        pedalController.update(delta);
        boolean pedaling = pedalController.isPedaling();
        float pps = pedalController.getPedaladasPorSegundo();

        // tracking de estatísticas
        if (pps > pedaladasPorSegundoMaxima) {
            pedaladasPorSegundoMaxima = pps;
        }
        somaCadencias += pps;
        contagemCadencias++;

        // atualiza background com base no pedal e na cadência
        updateBackground(delta, pedaling, pps);

        // pontos e como são calculados, porém esse é provisório, iremos mudar
        pontos += pps * delta * 10f;

        // anima player
        player.animateOnly(delta, pedaling);

        // verifica se troca de período
        checkLevelChange();

        // ATUALIZA O INIMIGO e verifica se pegou o jogador
        boolean jogadorCapturado = enemyManager.update(
            delta,
            playerX,
            playerY,
            player.getWidth(),
            player.getHeight(),
            pps
        );

        if (jogadorCapturado) {
            gameOver();
            return; // para o render
        }

        // DRAW
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBackground(batch);

        // Desenha o inimigo ANTES do player (para ficar atrás)
        enemyManager.render(batch, playerY);

        player.drawAt(batch, playerX, playerY);

        float distanciaInimigo = enemyManager.getDistanciaAteJogador(playerX);
        boolean emPerigo = enemyManager.jogadorEmPerigo(pps);
        float velocidadeMinima = enemyManager.getVelocidadeMinimaAtual();

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
            velocidadeMinima
        );

        batch.end();
    }


    //encerra o jogo e game over
    private void gameOver() {
        float cadenciaMedia = contagemCadencias > 0 ?
            somaCadencias / contagemCadencias : 0f;

        GameStats stats = new GameStats(
            tempoDecorrido,
            pedalController.getTotalPedaladas(),
            pontos,
            currentLevel,
            pedaladasPorSegundoMaxima,
            cadenciaMedia
        );

        game.setScreen(new GameOverScreen(game, stats));
    }

    private void updateBackground(float delta, boolean moving, float pps) {
        // curva mais suave + limite
        // até 6 pps vai aumentando, depois trava no máximo
        float speedMultiplier = 1f + Math.min(pps / 6f, 20.5f); // máximo é 20.5

        // se está pedalando muito pouco, não acelera o fundo
        if (pps < 0.5f) {
            speedMultiplier = 1f;
        }

        if (moving) {
            float bg1speed = bg1speedBase * speedMultiplier;
            float bg2speed = bg2speedBase * speedMultiplier;

            // o fundo se move só quando pedala
            bg1x -= bg1speed * delta;
            bg2x -= bg2speed * delta;
        }

        // loop do fundo
        if (bg1x <= -1280f) bg1x += 1280f;
        if (bg2x <= -1280f) bg2x += 1280f;

        // fade
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

        // timer do texto de nível
        if (levelTextTimer > 0f) {
            levelTextTimer -= delta;
            if (levelTextTimer < 0f) levelTextTimer = 0f;
        }
    }

    private void renderBackground(SpriteBatch batch) {
        // fundo base
        batch.draw(currentBg, bg1x, 0, 1280, 720);
        batch.draw(currentBg, bg1x + 1280f, 0, 1280, 720);
        batch.draw(currentBg, bg2x, 0, 1280, 720);
        batch.draw(currentBg, bg2x + 1280f, 0, 1280, 720);

        // fundo em transição
        if (transitioning && nextBg != null) {
            batch.setColor(1f, 1f, 1f, transitionAlpha);
            batch.draw(nextBg, bg1x, 0, 1280, 720);
            batch.draw(nextBg, bg1x + 1280f, 0, 1280, 720);
            batch.draw(nextBg, bg2x, 0, 1280, 720);
            batch.draw(nextBg, bg2x + 1280f, 0, 1280, 720);
            batch.setColor(Color.WHITE);
        }
    }

    private void checkLevelChange() {
        if (!transitioning) {
            if (tempoDecorrido > 5f && currentLevel == 1) {
                startBackgroundTransition(bgAfternoon);
                currentLevel = 2;
                enemyManager.setNivel(2); // ATUALIZA O INIMIGO
                showLevelText(2);
            } else if (tempoDecorrido > 15f && currentLevel == 2) {
                startBackgroundTransition(bgNight);
                currentLevel = 3;
                enemyManager.setNivel(3); // ATUALIZA O INIMIGO
                showLevelText(3);
            }
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
        if (bgDay != null) bgDay.dispose();
        if (bgAfternoon != null) bgAfternoon.dispose();
        if (bgNight != null) bgNight.dispose();

        player.dispose();
        hud.dispose();
        enemyManager.dispose();
    }
}
