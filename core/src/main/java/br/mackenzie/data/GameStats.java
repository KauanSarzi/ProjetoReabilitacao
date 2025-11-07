package br.mackenzie.data;

// salva/armazena as estatisticas da partida para mostrar no game over

public class GameStats {

    public final float tempoSobrevivido;
    public final int totalPedaladas;
    public final float pontosTotais;
    public final int nivelAlcancado;
    public final float pedaladasPorSegundoMaxima;
    public final float pedaladasPorSegundoMedia;

    public GameStats(float tempoSobrevivido,
                     int totalPedaladas,
                     float pontosTotais,
                     int nivelAlcancado,
                     float pedaladasPorSegundoMaxima,
                     float pedaladasPorSegundoMedia) {

        this.tempoSobrevivido = tempoSobrevivido;
        this.totalPedaladas = totalPedaladas;
        this.pontosTotais = pontosTotais;
        this.nivelAlcancado = nivelAlcancado;
        this.pedaladasPorSegundoMaxima = pedaladasPorSegundoMaxima;
        this.pedaladasPorSegundoMedia = pedaladasPorSegundoMedia;
    }

    public int calcularNota() {
        // calculo feito com: pontos baseados em tempo, pedaladas e nível
        float notaBase = (tempoSobrevivido * 2f) +
            (totalPedaladas * 0.5f) +
            (nivelAlcancado * 15f) +
            (pedaladasPorSegundoMaxima * 3f);

        return Math.min(100, Math.max(0, (int)notaBase));
    }

    // O titulo retornado sera de acordo com a performace, ajustaremos ainda as notas
    public String getTitulo() {
        int nota = calcularNota();

        if (nota >= 80) return "CICLISTA PROFISSIONAL";
        if (nota >= 60) return "PEDALADOR EXPERIENTE";
        if (nota >= 40) return "CICLISTA INICIANTE";
        if (nota >= 20) return "APRENDIZ DE PEDAL";
        return "PRIMEIRA TENTATIVA";
    }
}
