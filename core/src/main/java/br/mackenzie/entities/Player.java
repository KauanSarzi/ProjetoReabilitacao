package br.mackenzie.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    public float x, y;
    private Texture[] frames;
    private int frameIndex = 0;
    private float frameTime = 0f;
    private float frameDuration = 0.12f;
    private boolean animando = false;

    private float escala = 0.2f; // tamanho

    public Player(float startX, float startY) {
        x = startX;
        y = startY;

        frames = new Texture[]{
            new Texture(Gdx.files.internal("player/frame_1.png")),
            new Texture(Gdx.files.internal("player/frame_2.png")),
            new Texture(Gdx.files.internal("player/frame_3.png")),
            new Texture(Gdx.files.internal("player/frame_4.png"))
        };
    }

    public void animateOnly(float delta, boolean ligado) {
        animando = ligado;
        if (animando) {
            frameTime += delta;
            if (frameTime >= frameDuration) {
                frameTime = 0f;
                frameIndex++;
                if (frameIndex >= frames.length) frameIndex = 0;
            }
        } else {
            frameIndex = 0;
        }
    }

    public void drawAt(Batch batch, float drawX, float drawY) {
        Texture atual = frames[frameIndex];
        float largura = atual.getWidth() * escala;
        float altura = atual.getHeight() * escala;
        batch.draw(atual, drawX, drawY, largura, altura);
    }

    public int getWidth()  { return (int)(frames[0].getWidth() * escala); }
    public int getHeight() { return (int)(frames[0].getHeight() * escala); }

    public Rectangle getBounds(float drawX, float drawY) {
        float largura = frames[frameIndex].getWidth() * escala;
        float altura = frames[frameIndex].getHeight() * escala;
        return new Rectangle(drawX, drawY, largura, altura);
    }

    public void dispose() {
        for (Texture t : frames) t.dispose();
    }
}


