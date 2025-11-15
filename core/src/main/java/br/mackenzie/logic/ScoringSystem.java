package br.mackenzie.logic;

import com.badlogic.gdx.Gdx;

/**
 * Sistema de Pontuação baseado em Performance Híbrida
 *
 * CRITÉRIOS DE PONTUAÇÃO:
 * 1. Distância percorrida (progressão)
 * 2. Cadência média mantida (consistência)
 * 3. Tempo de conclusão de fase (eficiência)
 * 4. Bônus por completar fases
 * 5. Penalidade por tempo em perigo
 */
public class ScoringSystem {

    // ===== PONTUAÇÃO ACUMULADA =====
    private float pontosTotais = 0f;
    private float pontosDistancia = 0f;
    private float pontosCadencia = 0f;
    private float pontosEficiencia = 0f;
    private float bonusFases = 0f;

    // ===== MULTIPLICADORES =====
    private static final float PONTOS_POR_METRO = 1f;
    private static final float PONTOS_POR_PEDALADA = 5f;
    private static final float BONUS_FASE_BASE = 500f;
    private static final float PENALIDADE_PERIGO_POR_SEGUNDO = 2f;

    // ===== TRACKING DE PERFORMANCE =====
    private float tempoEmPerigo = 0f;
    private float tempoTotal = 0f;
    private int fasesCompletadas = 0;

    // ===== COMBO SYSTEM =====
    private float comboMultiplier = 1f;
    private float tempoSemPerigo = 0f;
    private static final float TEMPO_COMBO_TIER_1 = 10f;  // 10s sem perigo = 1.5x
    private static final float TEMPO_COMBO_TIER_2 = 30f;  // 30s sem perigo = 2.0x
    private static final float TEMPO_COMBO_TIER_3 = 60f;  // 60s sem perigo = 2.5x

    public ScoringSystem() {
        Gdx.app.log("ScoringSystem", "Sistema de pontuação inicializado");
    }

    /**
     * Atualiza a pontuação em tempo real
     *
     * @param delta Tempo decorrido
     * @param distanciaPercorrida Distância da fase atual
     * @param pedaladasPorSegundo Cadência atual
     * @param emPerigo Se está em situação de perigo
     */
    public void update(float delta, float distanciaPercorrida,
                       float pedaladasPorSegundo, boolean emPerigo) {

        tempoTotal += delta;

        // Atualiza combo multiplier
        if (emPerigo) {
            tempoEmPerigo += delta;
            tempoSemPerigo = 0f;
            comboMultiplier = 1f; // Reseta combo
        } else {
            tempoSemPerigo += delta;
            atualizarCombo();
        }

        // Pontos por distância (incremental)
        float pontosDistanciaFrame = (distanciaPercorrida * PONTOS_POR_METRO * delta) * comboMultiplier;
        pontosDistancia += pontosDistanciaFrame;

        // Pontos por cadência (premia consistência)
        float pontosCadenciaFrame = (pedaladasPorSegundo * PONTOS_POR_PEDALADA * delta) * comboMultiplier;
        pontosCadencia += pontosCadenciaFrame;

        // Recalcula total
        pontosTotais = pontosDistancia + pontosCadencia + pontosEficiencia + bonusFases;

        // Aplica penalidade por tempo em perigo
        float penalidade = tempoEmPerigo * PENALIDADE_PERIGO_POR_SEGUNDO;
        pontosTotais = Math.max(0, pontosTotais - penalidade);
    }

    /**
     * Atualiza o multiplicador de combo baseado no tempo sem perigo
     */
    private void atualizarCombo() {
        if (tempoSemPerigo >= TEMPO_COMBO_TIER_3) {
            comboMultiplier = 2.5f;
        } else if (tempoSemPerigo >= TEMPO_COMBO_TIER_2) {
            comboMultiplier = 2.0f;
        } else if (tempoSemPerigo >= TEMPO_COMBO_TIER_1) {
            comboMultiplier = 1.5f;
        } else {
            comboMultiplier = 1f;
        }
    }

    /**
     * Registra conclusão de fase e calcula bônus
     *
     * @param numeroFase Número da fase completada
     * @param tempoFase Tempo levado para completar
     * @param cadenciaMedia Cadência média mantida
     * @param cadenciaMinima Cadência mínima exigida
     */
    public void registrarFaseCompleta(int numeroFase, float tempoFase,
                                      float cadenciaMedia, float cadenciaMinima) {

        fasesCompletadas++;

        // Bônus base pela fase
        float bonusBase = BONUS_FASE_BASE * numeroFase;

        // Multiplicador por eficiência de tempo
        // Tempo ideal: 60s por fase. Menos tempo = mais bônus
        float tempoIdeal = 60f;
        float eficienciaTempo = Math.min(2f, tempoIdeal / tempoFase);

        // Multiplicador por performance de cadência
        // Quanto maior a cadência em relação ao mínimo, maior o bônus
        float eficienciaCadencia = Math.min(2f, cadenciaMedia / cadenciaMinima);

        // Calcula bônus total
        float bonusTotal = bonusBase * eficienciaTempo * eficienciaCadencia;

        bonusFases += bonusTotal;
        pontosEficiencia += bonusTotal * 0.5f; // 50% do bônus vai para eficiência

        Gdx.app.log("ScoringSystem",
            String.format("FASE %d COMPLETA! Bônus: +%.0f pts (Tempo: %.1fx, Cadência: %.1fx)",
                numeroFase,
                bonusTotal,
                eficienciaTempo,
                eficienciaCadencia
            )
        );

        // Recalcula total
        pontosTotais = pontosDistancia + pontosCadencia + pontosEficiencia + bonusFases;
    }

    /**
     * Calcula nota final (0-100)
     * Baseado em múltiplos fatores de performance
     */
    public int calcularNotaFinal(float tempoTotal, int fasesCompletadas,
                                 float cadenciaMaxima, float cadenciaMedia) {

        float nota = 0f;

        // 40 pontos por fases completadas (13.33 por fase)
        nota += (fasesCompletadas * 13.33f);

        // 30 pontos por pontuação total (escala logarítmica)
        float escalaPontos = Math.min(30f, (float) Math.log10(pontosTotais + 1) * 10f);
        nota += escalaPontos;

        // 15 pontos por cadência média (máximo em 8.0 ped/s)
        nota += Math.min(15f, (cadenciaMedia / 8f) * 15f);

        // 10 pontos por cadência máxima (máximo em 10.0 ped/s)
        nota += Math.min(10f, (cadenciaMaxima / 10f) * 10f);

        // 5 pontos por eficiência (menos tempo em perigo)
        float porcentagemSegura = 1f - (tempoEmPerigo / tempoTotal);
        nota += porcentagemSegura * 5f;

        return Math.min(100, Math.max(0, Math.round(nota)));
    }

    /**
     * Retorna análise detalhada da pontuação
     */
    public String getRelatorioDetalhado() {
        return String.format(
            "═══════════════════════════════════\n" +
                "      RELATÓRIO DE PONTUAÇÃO\n" +
                "═══════════════════════════════════\n" +
                "Pontos Totais:     %.0f\n" +
                "  ├─ Distância:    %.0f\n" +
                "  ├─ Cadência:     %.0f\n" +
                "  ├─ Eficiência:   %.0f\n" +
                "  └─ Bônus Fases:  %.0f\n" +
                "\n" +
                "Performance:\n" +
                "  ├─ Fases Completas: %d\n" +
                "  ├─ Tempo Seguro:    %.1fs\n" +
                "  ├─ Tempo Perigo:    %.1fs\n" +
                "  └─ Combo Máx:       %.1fx\n" +
                "═══════════════════════════════════",
            pontosTotais,
            pontosDistancia,
            pontosCadencia,
            pontosEficiencia,
            bonusFases,
            fasesCompletadas,
            tempoTotal - tempoEmPerigo,
            tempoEmPerigo,
            getComboMaximoAtingido()
        );
    }

    /**
     * Retorna título da performance baseado na nota
     */
    public String getTituloPerformance(int nota) {
        if (nota >= 95) return "🏆 LENDA DA REABILITAÇÃO";
        if (nota >= 85) return "⭐ ATLETA EXEMPLAR";
        if (nota >= 75) return "💪 CICLISTA DETERMINADO";
        if (nota >= 65) return "🚴 PEDALADOR DEDICADO";
        if (nota >= 50) return "✓ PROGRESSO CONSISTENTE";
        if (nota >= 35) return "↗️ EM DESENVOLVIMENTO";
        return "🌱 INÍCIO DA JORNADA";
    }

    /**
     * Retorna o maior combo atingido durante a sessão
     */
    private float getComboMaximoAtingido() {
        if (tempoSemPerigo >= TEMPO_COMBO_TIER_3) return 2.5f;
        if (tempoSemPerigo >= TEMPO_COMBO_TIER_2) return 2.0f;
        if (tempoSemPerigo >= TEMPO_COMBO_TIER_1) return 1.5f;
        return 1f;
    }

    /**
     * Retorna o próximo tier de combo e tempo restante
     */
    public String getProximoCombo() {
        if (tempoSemPerigo >= TEMPO_COMBO_TIER_3) {
            return "COMBO MAX! (2.5x)";
        } else if (tempoSemPerigo >= TEMPO_COMBO_TIER_2) {
            float faltam = TEMPO_COMBO_TIER_3 - tempoSemPerigo;
            return String.format("Próximo: 2.5x em %.0fs", faltam);
        } else if (tempoSemPerigo >= TEMPO_COMBO_TIER_1) {
            float faltam = TEMPO_COMBO_TIER_2 - tempoSemPerigo;
            return String.format("Próximo: 2.0x em %.0fs", faltam);
        } else {
            float faltam = TEMPO_COMBO_TIER_1 - tempoSemPerigo;
            return String.format("Próximo: 1.5x em %.0fs", faltam);
        }
    }

    // ===== GETTERS =====

    public float getPontosTotais() {
        return pontosTotais;
    }

    public float getComboMultiplier() {
        return comboMultiplier;
    }

    public float getTempoEmPerigo() {
        return tempoEmPerigo;
    }

    public float getTempoTotal() {
        return tempoTotal;
    }

    public int getFasesCompletadas() {
        return fasesCompletadas;
    }

    public String getBreakdownPontos() {
        return String.format(
            "Dist: %.0f | Cad: %.0f | Efic: %.0f | Bonus: %.0f",
            pontosDistancia,
            pontosCadencia,
            pontosEficiencia,
            bonusFases
        );
    }
}
