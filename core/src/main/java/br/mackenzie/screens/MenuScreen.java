package br.mackenzie.screens;

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
import br.mackenzie.Main;

public class MenuScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture bgTexture;
    private Texture titleTex, title2Tex, playTex, quitTex, play2Tex, spaceTex, escTex;
    private Image bgImage, titleImage, playBtn, quitBtn, play2Btn, spaceImage, escImage;

    private boolean showingControls = false;

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

        // imagens usadas nas duas telas
        titleTex = new Texture(Gdx.files.internal("images/title.png"));
        title2Tex = new Texture(Gdx.files.internal("images/title2.png"));
        playTex  = new Texture(Gdx.files.internal("images/play.png"));
        quitTex  = new Texture(Gdx.files.internal("images/quit.png"));
        play2Tex = new Texture(Gdx.files.internal("images/play2.png"));
        spaceTex = new Texture(Gdx.files.internal("images/space_bar.png"));
        escTex   = new Texture(Gdx.files.internal("images/esc.png"));
    }

    // ----------- MENU PRINCIPAL -----------
    private void buildMenuLayout() {
        stage.clear();
        if (bgImage != null) stage.addActor(bgImage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        titleImage = new Image(titleTex); // título original
        titleImage.setSize(600, 200);
        titleImage.getColor().a = 0f;
        titleImage.addAction(Actions.fadeIn(1f));

        playBtn = new Image(playTex);
        playBtn.setSize(200, 200);
        quitBtn = new Image(quitTex);
        quitBtn.setSize(150, 150);

        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                showControlLayout(); // muda para tela de controles
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

        Table buttonsRow = new Table();
        buttonsRow.add(playBtn).padRight(30f);
        buttonsRow.add(quitBtn);

        root.add(titleImage).padBottom(40f).row();
        root.add(buttonsRow).center();
    }

    // ----------- TELA DE CONTROLES -----------
    private void showControlLayout() {
        showingControls = true;
        stage.clear();
        if (bgImage != null) stage.addActor(bgImage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // título (title2.png) com as mesmas dimensões do title.png
        titleImage = new Image(title2Tex);
        titleImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        // estilo texto branco
        LabelStyle style = new LabelStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.WHITE;

        Label pedalLabel = new Label("[Pedalar]", style);
        Label pauseLabel = new Label("[Pausar]", style);

        spaceImage = new Image(spaceTex);
        escImage = new Image(escTex);

        // tabela vertical com controles (um embaixo do outro)
        Table controlsColumn = new Table();

        Table line1 = new Table();
        line1.add(spaceImage).size(200, 80).padRight(5);
        line1.add(pedalLabel).left();

        Table line2 = new Table();
        line2.add(escImage).size(120, 80).padRight(5);
        line2.add(pauseLabel).left();

        controlsColumn.add(line1).padBottom(5f).row();
        controlsColumn.add(line2);


        play2Btn = new Image(play2Tex);
        play2Btn.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        play2Btn.getColor().a = 0f; // começa invisível
        play2Btn.addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.parallel(
                Actions.fadeIn(0.8f),
                Actions.moveBy(0, 20, 0.8f)
            )
        ));

        play2Btn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                game.goToGame();
            }
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor fromActor) {
                play2Btn.addAction(Actions.scaleTo(1.1f, 1.1f, 0.15f));
            }
            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int pointer, Actor toActor) {
                play2Btn.addAction(Actions.scaleTo(1f, 1f, 0.15f));
            }
        });

        // layout principal com tamanhos consistentes
        root.add(titleImage).size(1500, 500).center().padTop(50f).padBottom(-90).row();
        root.add(controlsColumn).center().padBottom(100f).row();
        root.add(play2Btn).size(400, 400).center().padBottom(90f).padTop(-150).row();
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
        if (title2Tex != null) title2Tex.dispose();
        if (playTex != null) playTex.dispose();
        if (quitTex != null) quitTex.dispose();
        if (play2Tex != null) play2Tex.dispose();
        if (spaceTex != null) spaceTex.dispose();
        if (escTex != null) escTex.dispose();
    }
}
