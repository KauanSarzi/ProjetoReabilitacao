package br.mackenzie.logic;

import br.mackenzie.entities.Enemy;

public class EnemyManager {

    private Enemy enemy;
    private int nivelAtual = 1;

    // Velocidades minimas necessarias para NÃO ser pego (em pedaladas/segundo)
    private static final float VELOCIDADE_MINIMA_NIVEL_1 = 2.0f;
    private static final float VELOCIDADE_MINIMA_NIVEL_2 = 3.5f;
    private static final float VELOCIDADE_MINIMA_NIVEL_3 = 5.0f;

    // Velocidade base do inimigo
    private static final float VELOCIDADE_INIMIGO_BASE = 100f;

    // Posição inicial do inimigo
    private static final float POSICAO_INICIAL_X = -200f;

    public EnemyManager(float startY) {
        enemy = new Enemy(POSICAO_INICIAL_X, startY);
        atualizarVelocidadeInimigo();
    }

    public void setNivel(int nivel) {
        if (this.nivelAtual != nivel) {
            this.nivelAtual = nivel;
            atualizarVelocidadeInimigo();
        }
    }

    private void atualizarVelocidadeInimigo() {
        float multiplicador = 1f;

        switch (nivelAtual) {
            case 1:
                multiplicador = 0.8f;
                break;
            case 2:
                multiplicador = 1.2f;
                break;
            case 3:
                multiplicador = 1.6f;
                break;
        }

        enemy.setVelocidade(VELOCIDADE_INIMIGO_BASE * multiplicador);
    }

    /**
     * Atualiza o inimigo com suporte a multiplicador dinâmico de velocidade
     * @param speedMultiplier Multiplicador aplicado quando jogador está abaixo da cadência mínima
     * @return true se o inimigo alcançou o jogador
     */
    public boolean update(float delta, float playerX, float playerY,
                          int playerWidth, int playerHeight,
                          float pedaladasPorSegundo,
                          float speedMultiplier) {

        enemy.update(delta);

        float velocidadeMinima = getVelocidadeMinimaAtual();

        if (pedaladasPorSegundo < velocidadeMinima) {
            // Aplica o multiplicador quando jogador está lento
            float velocidadeAjustada = enemy.getVelocidade() * speedMultiplier;
            float velocidadeOriginal = enemy.getVelocidade();

            enemy.setVelocidade(velocidadeAjustada);
            enemy.perseguir(delta, playerX);

            // Restaura velocidade original para próximo frame
            enemy.setVelocidade(velocidadeOriginal);
        } else {
            // Jogador rápido o suficiente, inimigo recua
            enemy.perseguir(delta * -0.3f, playerX);
        }

        // Verifica colisão
        return enemy.colideCom(playerX, playerY, playerWidth, playerHeight);
    }

    public float getVelocidadeMinimaAtual() {
        switch (nivelAtual) {
            case 1: return VELOCIDADE_MINIMA_NIVEL_1;
            case 2: return VELOCIDADE_MINIMA_NIVEL_2;
            case 3: return VELOCIDADE_MINIMA_NIVEL_3;
            default: return VELOCIDADE_MINIMA_NIVEL_1;
        }
    }

    public float getDistanciaAteJogador(float playerX) {
        return enemy.distanciaAte(playerX);
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, float enemyY) {
        if (enemy.x > -enemy.getWidth()) {
            enemy.drawAt(batch, enemy.x, enemyY);
        }
    }

    public boolean jogadorEmPerigo(float pedaladasPorSegundo) {
        return pedaladasPorSegundo < getVelocidadeMinimaAtual();
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void dispose() {
        enemy.dispose();
    }
}
