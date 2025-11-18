package br.mackenzie.logic;

/**
 * Gerencia a progressão de fases baseada em DISTÂNCIA + CADÊNCIA.
 * Cada fase requer:
 * - Distância mínima percorrida
 * - Cadência média mínima mantida
 *
 * Se a cadência instantânea cair abaixo da mínima, o Enemy acelera.
 */
public class PhaseManager {

    private int faseAtual = 1;
    private float distanciaPercorrida = 0f;

    // Acumuladores para calcular cadência média
    private float somaCadencias = 0f;
    private int contagemCadencias = 0;

    // Definição das fases (configurável)
    private static final FaseConfig[] FASES = {
        new FaseConfig(1, 50f, 2.0f),   // Fase 1: 50m, 2.0 ped/s
        new FaseConfig(2, 100f, 3.5f),  // Fase 2: 100m, 3.5 ped/s
        new FaseConfig(3, 150f, 5.0f)   // Fase 3: 150m, 5.0 ped/s
    };

    // Classe interna para configuração de fase
    private static class FaseConfig {
        final int numero;
        final float distanciaMinima;
        final float cadenciaMinima;

        FaseConfig(int numero, float distanciaMinima, float cadenciaMinima) {
            this.numero = numero;
            this.distanciaMinima = distanciaMinima;
            this.cadenciaMinima = cadenciaMinima;
        }
    }

    /**
     * Atualiza a progressão da fase atual.
     * @param delta Tempo decorrido desde o último frame
     * @param pedaladasPorSegundo Cadência instantânea
     * @return true se houve mudança de fase
     */
    public boolean update(float delta, float pedaladasPorSegundo) {
        // Atualiza distância (simulação: 1 ped/s = 2 metros/s)
        distanciaPercorrida += pedaladasPorSegundo * 2f * delta;

        // Atualiza média de cadência
        somaCadencias += pedaladasPorSegundo;
        contagemCadencias++;

        // Verifica se completou a fase atual
        if (faseAtual <= FASES.length && verificarConclusaoFase()) {
            proximaFase();
            return true;
        }

        return false;
    }

    /**
     * Verifica se as condições para completar a fase foram atingidas
     */
    private boolean verificarConclusaoFase() {
        FaseConfig fase = getFaseConfig(faseAtual);

        float cadenciaMedia = getCadenciaMedia();

        return distanciaPercorrida >= fase.distanciaMinima &&
            cadenciaMedia >= fase.cadenciaMinima;
    }

    /**
     * Avança para a próxima fase e reseta contadores
     */
    private void proximaFase() {
        faseAtual++;
        distanciaPercorrida = 0f;
        somaCadencias = 0f;
        contagemCadencias = 0;
    }

    /**
     * Retorna o multiplicador de velocidade do Enemy baseado na cadência atual
     * @param pedaladasPorSegundo Cadência instantânea
     * @return Multiplicador (1.0 = velocidade normal, >1.0 = mais rápido)
     */
    public float getEnemySpeedMultiplier(float pedaladasPorSegundo) {
        FaseConfig fase = getFaseConfig(faseAtual);

        if (pedaladasPorSegundo < fase.cadenciaMinima) {
            // Quanto mais abaixo da mínima, mais rápido o Enemy
            float deficit = fase.cadenciaMinima - pedaladasPorSegundo;
            return 1f + (deficit * 0.3f); // 30% mais rápido por cada ped/s abaixo
        }

        return 1f; // Velocidade normal
    }

    /**
     * Retorna a cadência média da fase atual
     */
    public float getCadenciaMedia() {
        return contagemCadencias > 0 ? somaCadencias / contagemCadencias : 0f;
    }

    /**
     * Retorna o progresso percentual da distância na fase atual (0.0 a 1.0)
     */
    public float getProgressoDistancia() {
        FaseConfig fase = getFaseConfig(faseAtual);
        return Math.min(1f, distanciaPercorrida / fase.distanciaMinima);
    }

    /**
     * Retorna o progresso percentual da cadência na fase atual (0.0 a 1.0+)
     */
    public float getProgressoCadencia() {
        FaseConfig fase = getFaseConfig(faseAtual);
        float media = getCadenciaMedia();
        return media / fase.cadenciaMinima;
    }

    /**
     * Verifica se o jogador está em risco (cadência abaixo da mínima)
     */
    public boolean isEmRisco(float pedaladasPorSegundo) {
        FaseConfig fase = getFaseConfig(faseAtual);
        return pedaladasPorSegundo < fase.cadenciaMinima;
    }

    // === GETTERS ===

    public int getFaseAtual() {
        return faseAtual;
    }

    /**
     * Retorna o último nível jogável completado (para estatísticas)
     * Se faseAtual > FASES.length, retorna o número da última fase
     */
    public int getNivelCompletado() {
        if (faseAtual > FASES.length) {
            return FASES.length; // Retorna 3 (última fase)
        }
        return Math.max(1, faseAtual - 1);
    }

    public float getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public float getDistanciaMinima() {
        return getFaseConfig(faseAtual).distanciaMinima;
    }

    public float getCadenciaMinima() {
        return getFaseConfig(faseAtual).cadenciaMinima;
    }

    /**
     * Agora este método significa:
     * "todas as fases já foram concluídas?"
     * Ele só retorna true DEPOIS de terminar a última fase jogável.
     */
    public boolean isUltimaFase() {
        return faseAtual > FASES.length;
    }

    /**
     * Retorna a configuração da fase especificada
     */
    private FaseConfig getFaseConfig(int numeroFase) {
        int index = numeroFase - 1;
        if (index < 0 || index >= FASES.length) {
            return FASES[FASES.length - 1]; // Retorna última fase se inválido
        }
        return FASES[index];
    }
}
