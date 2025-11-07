package br.mackenzie.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Representa o inimigo que persegue o jogador.
 * Possui animação própria e sistema de velocidade.
 */
public class Enemy {

    public float x, y;
    private Texture[] frames;
    private int frameIndex = 0;
    private float frameTime = 0f;
    private float frameDuration = 0.15f;

    private float escala = 0.25f; // ligeiramente maior que o player
    private float velocidade = 0f;

    public Enemy(float startX, float startY) {
        x = startX;
        y = startY;

        // Carrega frames do inimigo (assumindo que você terá sprites próprios)
        // Por enquanto, vou usar um esquema genérico
        frames = new Texture[]{
            new Texture(Gdx.files.internal("enemy/frame_1.png")),
            new Texture(Gdx.files.internal("enemy/frame_2.png")),
            new Texture(Gdx.files.internal("enemy/frame_3.png")),
            new Texture(Gdx.files.internal("enemy/frame_4.png"))
        };
    }

    /**
     * Atualiza a animação do inimigo
     */
    public void update(float delta) {
        frameTime += delta;
        if (frameTime >= frameDuration) {
            frameTime = 0f;
            frameIndex++;
            if (frameIndex >= frames.length) frameIndex = 0;
        }
    }

    /**
     * Move o inimigo em direção ao jogador
     * @param delta tempo decorrido
     * @param playerX posição X do jogador (alvo)
     */
    public void perseguir(float delta, float playerX) {
        if (x < playerX) {
            x += velocidade * delta;
        }
    }

    /**
     * Define a velocidade de perseguição baseada no nível
     */
    public void setVelocidade(float novaVelocidade) {
        this.velocidade = novaVelocidade;
    }

    public float getVelocidade() {
        return velocidade;
    }

    /**
     * Desenha o inimigo na tela
     */
    public void drawAt(Batch batch, float drawX, float drawY) {
        Texture atual = frames[frameIndex];
        float largura = atual.getWidth() * escala;
        float altura = atual.getHeight() * escala;
        batch.draw(atual, drawX, drawY, largura, altura);
    }

    /**
     * Verifica colisão simples com o jogador
     */
    public boolean colideCom(float playerX, float playerY, int playerWidth, int playerHeight) {
        float enemyWidth = frames[0].getWidth() * escala;
        float enemyHeight = frames[0].getHeight() * escala;

        return x < playerX + playerWidth &&
            x + enemyWidth > playerX &&
            y < playerY + playerHeight &&
            y + enemyHeight > playerY;
    }

    /**
     * Calcula distância até o jogador
     */
    public float distanciaAte(float playerX) {
        return Math.abs(playerX - x);
    }

    public int getWidth()  { return (int)(frames[0].getWidth() * escala); }
    public int getHeight() { return (int)(frames[0].getHeight() * escala); }

    public void dispose() {
        for (Texture t : frames) t.dispose();
    }
}
