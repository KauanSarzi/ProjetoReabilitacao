package br.mackenzie.screens;

import br.mackenzie.Main;
import br.mackenzie.data.GameStats;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

//tela exibida quando o inimigo pega o jogador
public class GameOverScreen extends ScreenAdapter {

    private final Main game;
    private final GameStats stats;

    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture bgTexture;
    private Image bgImage;
    private Texture restartTex, quitTex;
    private Image restartBtn, quitBtn;

    private BitmapFont fontTitle, fontStats, fontSmall;
    private LabelStyle styleTitulo, styleStats, styleSmall;

    public GameOverScreen(Main game, GameStats stats) {
        this.game = game;
        this.stats = stats;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        buildLayout();

        // Fade-in inicial
        stage.getRoot().getColor().a = 0f;
        stage.getRoot().addAction(Actions.fadeIn(0.1f));
    }

    private void loadAssets() {
        // Background
        try {
            bgTexture = new Texture(Gdx.files.internal("images/bg_calm.png"));
            bgImage = new Image(bgTexture);
            bgImage.setFillParent(true);
            bgImage.getColor().a = 0.7f; // mais escuro para dar destaque ao texto
            stage.addActor(bgImage);
        } catch (Exception ignored) {}

        // Botões
        restartTex = new Texture(Gdx.files.internal("images/restart.png"));
        quitTex = new Texture(Gdx.files.internal("images/quit.png"));

        // Fontes
        fontTitle = new BitmapFont();
        fontTitle.getData().setScale(4f);
        fontTitle.setColor(Color.RED);

        fontStats = new BitmapFont();
        fontStats.getData().setScale(2.5f);
        fontStats.setColor(Color.WHITE);

        fontSmall = new BitmapFont();
        fontSmall.getData().setScale(1.8f);
        fontSmall.setColor(Color.LIGHT_GRAY);

        styleTitulo = new LabelStyle(fontTitle, Color.RED);
        styleStats = new LabelStyle(fontStats, Color.WHITE);
        styleSmall = new LabelStyle(fontSmall, Color.LIGHT_GRAY);
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Título "GAME OVER"
        Label titleLabel = new Label("GAME OVER", styleTitulo);
        titleLabel.getColor().a = 0f;
        titleLabel.addAction(Actions.sequence(
            Actions.delay(0.2f),
            Actions.fadeIn(0.5f)
        ));

        // Título da performance
        Label tituloPerformance = new Label(stats.getTitulo(), styleStats);
        tituloPerformance.setColor(Color.GOLD);
        tituloPerformance.getColor().a = 0f;
        tituloPerformance.addAction(Actions.sequence(
            Actions.delay(0.7f),
            Actions.fadeIn(0.5f)
        ));

        // Estatísticas
        Table statsTable = new Table();
        statsTable.getColor().a = 0f;
        statsTable.addAction(Actions.sequence(
            Actions.delay(1.2f),
            Actions.fadeIn(0.8f)
        ));

        statsTable.add(new Label("Tempo Sobrevivido:", styleSmall)).left().padRight(20f);
        statsTable.add(new Label(String.format("%.1f segundos", stats.tempoSobrevivido), styleStats)).right().row();

        statsTable.add(new Label("Pedaladas Totais:", styleSmall)).left().padRight(20f).padTop(10f);
        statsTable.add(new Label(String.format("%d", stats.totalPedaladas), styleStats)).right().padTop(10f).row();

        statsTable.add(new Label("Pontos:", styleSmall)).left().padRight(20f).padTop(10f);
        statsTable.add(new Label(String.format("%.0f", stats.pontosTotais), styleStats)).right().padTop(10f).row();

        statsTable.add(new Label("Nível Alcançado:", styleSmall)).left().padRight(20f).padTop(10f);
        statsTable.add(new Label(String.format("%d", stats.nivelAlcancado), styleStats)).right().padTop(10f).row();

        statsTable.add(new Label("Cadência Máxima:", styleSmall)).left().padRight(20f).padTop(10f);
        statsTable.add(new Label(String.format("%.1f ped/s", stats.pedaladasPorSegundoMaxima), styleStats)).right().padTop(10f).row();

        statsTable.add(new Label("Cadência Média:", styleSmall)).left().padRight(20f).padTop(10f);
        statsTable.add(new Label(String.format("%.1f ped/s", stats.pedaladasPorSegundoMedia), styleStats)).right().padTop(10f).row();

        // Nota final
        Label notaLabel = new Label(String.format("NOTA: %d/100", stats.calcularNota()), styleStats);
        notaLabel.setColor(Color.CYAN);
        notaLabel.getColor().a = 0f;
        notaLabel.addAction(Actions.sequence(
            Actions.delay(2f),
            Actions.fadeIn(0.5f)
        ));

        // Botões
        restartBtn = new Image(restartTex);
        restartBtn.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        quitBtn = new Image(quitTex);
        quitBtn.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        setupButtonListeners();

        Table buttonsRow = new Table();
        buttonsRow.add(restartBtn).size(400, 400).padRight(25f);
        buttonsRow.add(quitBtn).size(380, 380);
        buttonsRow.getColor().a = 0f;
        buttonsRow.addAction(Actions.sequence(
            Actions.delay(2.5f),
            Actions.fadeIn(0.5f)
        ));

        root.add(titleLabel).padBottom(4f).row();
        root.add(tituloPerformance).padBottom(5f).row();
        root.add(statsTable).padBottom(5f).row();
        root.add(notaLabel).padBottom(6f).row();
        root.add(buttonsRow).padTop(-140f).padBottom(-100f).center().row();
    }

    private void setupButtonListeners() {
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor fromActor) {
                restartBtn.addAction(Actions.scaleTo(1.1f, 1.1f, 0.15f));
            }
            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor toActor) {
                restartBtn.addAction(Actions.scaleTo(1f, 1f, 0.15f));
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor fromActor) {
                quitBtn.addAction(Actions.scaleTo(1.1f, 1.1f, 0.15f));
            }
            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor toActor) {
                quitBtn.addAction(Actions.scaleTo(1f, 1f, 0.15f));
            }
        });
    }

    @Override
    public void render(float delta) {
        if (bgTexture == null) {
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (restartTex != null) restartTex.dispose();
        if (quitTex != null) quitTex.dispose();
        fontTitle.dispose();
        fontStats.dispose();
        fontSmall.dispose();
    }
}
