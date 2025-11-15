package br.mackenzie.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Hud {

    private final BitmapFont font;
    private final GlyphLayout layout;
    private final ShapeRenderer shapeRenderer;

    public Hud() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch,
                       float tempo,
                       float pedaladasPorSegundo,
                       float pontos,
                       int totalPedaladas,
                       String levelText,
                       float levelTextTimer,
                       float distanciaInimigo,
                       boolean emPerigo,
                       float velocidadeMinima,
                       float distanciaPercorrida,
                       float distanciaMinima,
                       float progressoDistancia,
                       float cadenciaMedia,
                       float progressoCadencia) {

        // === INFORMAÇÕES BÁSICAS (CANTO SUPERIOR ESQUERDO) ===
        font.draw(batch, String.format("Tempo: %.1fs", tempo), 40, 700);
        font.draw(batch, String.format("Pedaladas/s: %.1f", pedaladasPorSegundo), 40, 660);
        font.draw(batch, String.format("Pontos: %.0f", pontos), 40, 620);
        font.draw(batch, String.format("Pedaladas totais: %d", totalPedaladas), 40, 580);

        // Indicador de velocidade necessária
        Color corVelocidade = emPerigo ? Color.RED : Color.GREEN;
        font.setColor(corVelocidade);
        font.draw(batch, String.format("Vel. Mínima: %.1f ped/s", velocidadeMinima), 40, 540);
        font.setColor(Color.WHITE);

        // Aviso de perigo
        if (emPerigo) {
            font.setColor(Color.RED);
            font.draw(batch, "⚠ INIMIGO ACELERANDO!", 40, 500);
            font.setColor(Color.WHITE);
        }

        // === PROGRESSO DE DISTÂNCIA (CANTO SUPERIOR DIREITO) ===
        font.draw(batch, String.format("Distância: %.0f/%.0fm",
            distanciaPercorrida, distanciaMinima), 900, 700);

        font.draw(batch, String.format("Cadência Média: %.1f ped/s",
            cadenciaMedia), 900, 660);

        // Texto central de nível
        if (levelTextTimer > 0f) {
            float oldX = font.getData().scaleX;
            float oldY = font.getData().scaleY;
            font.getData().setScale(3f);

            layout.setText(font, levelText);
            float x = (1280 - layout.width) / 2f;
            float y = (720 + layout.height) / 2f;
            font.draw(batch, layout, x, y);

            font.getData().setScale(oldX, oldY);
        }

        // Pausa o batch para desenhar shapes
        batch.end();

        // === BARRAS DE PROGRESSO ===
        renderBarraDistancia(distanciaInimigo, emPerigo);
        renderBarraProgressoFase(progressoDistancia, progressoCadencia);

        batch.begin();
    }

    /**
     * Desenha uma barra visual mostrando a distância do inimigo
     */
    private void renderBarraDistancia(float distancia, boolean perigo) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fundo da barra
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(1080, 40, 180, 30);

        // Barra de distância
        float porcentagem = Math.min(1f, distancia / 800f);
        Color corBarra = perigo ? Color.RED : Color.GREEN;

        shapeRenderer.setColor(corBarra);
        shapeRenderer.rect(1082, 42, 176 * porcentagem, 26);

        shapeRenderer.end();

        // Texto "INIMIGO"
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);

        SpriteBatch tempBatch = new SpriteBatch();
        tempBatch.begin();
        font.draw(tempBatch, "INIMIGO", 1090, 95);
        tempBatch.end();
        tempBatch.dispose();

        font.getData().setScale(2f);
    }

    /**
     * Desenha barras de progresso para distância e cadência média da fase
     */
    private void renderBarraProgressoFase(float progressoDistancia,
                                          float progressoCadencia) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // === BARRA DE DISTÂNCIA ===
        float barX = 400;
        float barY = 50;
        float barWidth = 400;
        float barHeight = 25;

        // Fundo
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Preenchimento
        Color corDistancia = progressoDistancia > 0.5f ? Color.GREEN : Color.YELLOW;
        shapeRenderer.setColor(corDistancia);
        shapeRenderer.rect(barX + 2, barY + 2,
            (barWidth - 4) * Math.min(1f, progressoDistancia),
            barHeight - 4);

        // === BARRA DE CADÊNCIA MÉDIA ===
        barY = 15;

        // Fundo
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Preenchimento
        Color corCadencia = progressoCadencia >= 1f ? Color.GREEN : Color.RED;
        shapeRenderer.setColor(corCadencia);
        shapeRenderer.rect(barX + 2, barY + 2,
            (barWidth - 4) * Math.min(1f, progressoCadencia),
            barHeight - 4);

        shapeRenderer.end();

        // Labels das barras
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);

        SpriteBatch tempBatch = new SpriteBatch();
        tempBatch.begin();
        font.draw(tempBatch, "DISTÂNCIA", barX + 5, 90);
        font.draw(tempBatch, "CADÊNCIA", barX + 5, 55);
        tempBatch.end();
        tempBatch.dispose();

        font.getData().setScale(2f);
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
