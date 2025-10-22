package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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

public class MenuScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture bgTexture;
    private Texture titleTex, playTex, quitTex;
    private Image bgImage, titleImage, playBtn, quitBtn;

    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        loadTextures();
        buildMenuLayout();

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

        titleTex = new Texture(Gdx.files.internal("images/title.png"));
        playTex  = new Texture(Gdx.files.internal("images/play.png"));
        quitTex  = new Texture(Gdx.files.internal("images/quit.png"));
    }

    private void buildMenuLayout() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        //  título PNG
        titleImage = new Image(titleTex);
        titleImage.setSize(600, 200);
        titleImage.getColor().a = 0f;
        titleImage.addAction(Actions.fadeIn(1f));

        //  botões PNG
        playBtn = new Image(playTex);
        playBtn.setSize(200, 200);
        quitBtn = new Image(quitTex);
        quitBtn.setSize(150, 150);

        // comportamento Play
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                game.goToGame();
            }
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor fromActor) {
                playBtn.addAction(Actions.scaleTo(1.1f, 1.1f, 0.15f));
            }
            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor toActor) {
                playBtn.addAction(Actions.scaleTo(1f, 1f, 0.15f));
            }
        });

        // comportamento Quit
        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                Gdx.app.exit();
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

        // tabela para os botões lado a lado
        Table buttonsRow = new Table();
        buttonsRow.add(playBtn).padRight(30f);
        buttonsRow.add(quitBtn);

        // estrutura vertical geral: título em cima, botões abaixo
        root.add(titleImage).padBottom(40f).row();
        root.add(buttonsRow).center();

        //podemos adc sensaçao de batimento cardiaco no titulo

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
        if (playTex != null) playTex.dispose();
        if (quitTex != null) quitTex.dispose();
    }
}
