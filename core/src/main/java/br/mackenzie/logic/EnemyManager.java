package br.mackenzie.logic;

import br.mackenzie.entities.Enemy;

// a cada nivel o inimigo tera uma dificuldade
public class EnemyManager {

    private Enemy enemy;
    private int nivelAtual = 1;

    // Velocidades minimas necessarias para NÃO ser pego (em pedaladas/segundo)
    private static final float VELOCIDADE_MINIMA_NIVEL_1 = 2.0f;  // Nivel 1: 2 pedaladas/s
    private static final float VELOCIDADE_MINIMA_NIVEL_2 = 3.5f;  // 2: 3.5 pedaladas/s
    private static final float VELOCIDADE_MINIMA_NIVEL_3 = 5.0f;  // 3: 5 pedaladas/s

    //lembrar de perguntar ao prov se muda ou nao, pois sera na bike

    // Velocidade base do inimigo
    private static final float VELOCIDADE_INIMIGO_BASE = 100f;

    // Posição inicial do inimigo, avaliar se deixa ele fora da tela, o apenas no fim da tela
    private static final float POSICAO_INICIAL_X = -200f;

    public EnemyManager(float startY) {
        enemy = new Enemy(POSICAO_INICIAL_X, startY);
        atualizarVelocidadeInimigo();
    }

    //atualiza o nivel e a veloc do enemy
    public void setNivel(int nivel) {
        if (this.nivelAtual != nivel) {
            this.nivelAtual = nivel;
            atualizarVelocidadeInimigo();
        }
    }


    // ajusta a velocidade do inimigo de acordo com o nivel
    private void atualizarVelocidadeInimigo() {
        float multiplicador = 1f;

        switch (nivelAtual) {
            case 1:
                multiplicador = 0.8f;  // Mais lento no nível 1
                break;
            case 2:
                multiplicador = 1.2f;  // Velocidade média no nível 2
                break;
            case 3:
                multiplicador = 1.6f;  // Mais rápido no nível 3
                break;
        }

        enemy.setVelocidade(VELOCIDADE_INIMIGO_BASE * multiplicador);
    }

    // retorna true se o inimigo alcançar o player
    // faz a animaçao
    public boolean update(float delta, float playerX, float playerY,
                          int playerWidth, int playerHeight,
                          float pedaladasPorSegundo) {

        enemy.update(delta);

        // O inimigo só avança se o jogador está ABAIXO da velocidade mínima do nível
        float velocidadeMinima = getVelocidadeMinimaAtual();

        if (pedaladasPorSegundo < velocidadeMinima) {
            // Se o jogador estiver lento demais para o nivel Enemy avança
            enemy.perseguir(delta, playerX);
        } else {
            // Jogador está rápido o suficiente, inimigo mantém distância
            // (opcionalmente, pode até recuar um pouco)
            enemy.perseguir(delta * -0.3f, playerX); // recua devagar
        }

        // Verifica colisão
        return enemy.colideCom(playerX, playerY, playerWidth, playerHeight);
    }

    // Retorna a velocidade mínima necessária para o nivel atual
    public float getVelocidadeMinimaAtual() {
        switch (nivelAtual) {
            case 1: return VELOCIDADE_MINIMA_NIVEL_1;
            case 2: return VELOCIDADE_MINIMA_NIVEL_2;
            case 3: return VELOCIDADE_MINIMA_NIVEL_3;
            default: return VELOCIDADE_MINIMA_NIVEL_1;
        }
    }

    /**
     * Retorna a distância atual entre inimigo e jogador
     */
    public float getDistanciaAteJogador(float playerX) {
        return enemy.distanciaAte(playerX);
    }

    /**
     * Desenha o inimigo apenas se ele está visível (entrou na tela)
     */
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, float enemyY) {
        if (enemy.x > -enemy.getWidth()) { // só desenha se está pelo menos parcialmente visível
            enemy.drawAt(batch, enemy.x, enemyY);
        }
    }

    /**
     * Verifica se o jogador está em perigo (velocidade insuficiente)
     */
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
