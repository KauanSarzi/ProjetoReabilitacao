package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import br.mackenzie.Main;

public class PauseScreen extends ScreenAdapter {

    private final Main game;
    private final GameScreen previousGame;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture bgTexture;
    private Texture titleTex, continueTex, restartTex;
    private Image bgImage, titleImage, continueBtn, restartBtn;

    public PauseScreen(Main game, GameScreen previousGame) {
        this.game = game;
        this.previousGame = previousGame;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        loadTextures();
        buildPauseLayout();

        // Fade-in inicial
        stage.getRoot().getColor().a = 0f;
        stage.getRoot().addAction(Actions.fadeIn(0.8f));
    }

    private void loadTextures() {
        try {
            bgTexture = new Texture(Gdx.files.internal("images/bg_calm.png"));
            bgImage = new Image(bgTexture);
            bgImage.setFillParent(true);
            bgImage.getColor().a = 0.95f;
            stage.addActor(bgImage);
        } catch (Exception ignored) {}

        // Carrega botões
        titleTex     = new Texture(Gdx.files.internal("images/title.png")); // opcional (pode criar "Pause" PNG)
        continueTex  = new Texture(Gdx.files.internal("images/continue.png"));
        restartTex   = new Texture(Gdx.files.internal("images/restart.png"));
    }

    private void buildPauseLayout() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Título “Pause” (ou pode remover se quiser só os botões)
        titleImage = new Image(titleTex);
        titleImage.setSize(500, 150);
        titleImage.getColor().a = 0f;
        titleImage.addAction(Actions.fadeIn(1f));

        // Botões
        continueBtn = new Image(continueTex);
        continueBtn.setSize(220, 220);

        restartBtn = new Image(restartTex);
        restartBtn.setSize(200, 200);



        // Retomar o jogo
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                game.setScreen(previousGame); // volta ao jogo pausado
            }
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor fromActor) {
                continueBtn.addAction(Actions.scaleTo(1.1f, 1.1f, 0.15f));
            }
            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor toActor) {
                continueBtn.addAction(Actions.scaleTo(1f, 1f, 0.15f));
            }
        });

        // Reiniciar o jogo
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                game.setScreen(new GameScreen(game)); // novo jogo do zero
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

        // ===== LAYOUT =====
        Table buttonsRow = new Table();
        buttonsRow.add(continueBtn).padRight(40f);
        buttonsRow.add(restartBtn);

        root.add(titleImage).padBottom(50f).row();
        root.add(buttonsRow).center();
    }

    @Override
    public void render(float delta) {
        if (bgTexture == null) {
            Gdx.gl.glClearColor(0.78f, 0.94f, 0.90f, 1f);
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
        if (titleTex != null) titleTex.dispose();
        if (continueTex != null) continueTex.dispose();
        if (restartTex != null) restartTex.dispose();
    }
}
