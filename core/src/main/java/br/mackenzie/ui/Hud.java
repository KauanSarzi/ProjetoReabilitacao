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
        font.getData().setScale(1.8f);   // tamanho base
        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch,
                       float tempo,                 // não exibido na HUD (pode usar na tela final)
                       float pedaladasPorSegundo,
                       float pontos,                // idem
                       int totalPedaladas,          // idem
                       String levelText,
                       float levelTextTimer,
                       float distanciaInimigo,      // mantido na assinatura, não usado aqui
                       boolean emPerigo,
                       float velocidadeMinima,      // cadência mínima do nível
                       float distanciaPercorrida,
                       float distanciaMinima,
                       float progressoDistancia,
                       float cadenciaMedia,         // usar na tela de resultado
                       float progressoCadencia) {   // não usado aqui

        // garante que nada fique transparente
        batch.setColor(Color.WHITE);

        // ==========================
        // BLOCO ESQUERDO – CADÊNCIA
        // ==========================

        // Título "Cadência"
        font.getData().setScale(2.0f);
        font.setColor(emPerigo ? Color.ORANGE : Color.WHITE);
        font.draw(batch, "Cadência", 40, 700);

        // Valores (itens)
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        font.draw(batch,
            String.format("• Atual: %.1f ped/s", pedaladasPorSegundo),
            40, 670);

        font.draw(batch,
            String.format("• Mínima: %.1f ped/s", velocidadeMinima),
            40, 645);

        // Alerta só quando estiver abaixo da mínima
        if (emPerigo) {
            font.setColor(Color.RED);
            font.getData().setScale(1.4f);
            font.draw(batch,
                "⚠ Cadência abaixo do ideal",
                40, 615);
            font.setColor(Color.WHITE);
        }

        // ============================
        // BLOCO DIREITO – DISTÂNCIA
        // ============================

        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Distância", 900, 700);

        font.getData().setScale(1.5f);
        font.draw(batch,
            String.format("• %.0f / %.0f m",
                distanciaPercorrida, distanciaMinima),
            900, 670);

        // ============================
        // TEXTO CENTRAL – LEVEL / FASE
        // ============================

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

        // ============================
        // BARRA DE PROGRESSO (DISTÂNCIA)
        // ============================

        batch.end();

        // Usa a mesma projeção do batch (câmera do jogo)
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        renderBarraDistanciaFase(progressoDistancia);

        batch.begin();
    }

    /**
     * ÚNICA barra na tela: progresso da distância da fase.
     */
    private void renderBarraDistanciaFase(float progressoDistancia) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float barX = 400;
        float barY = 40;
        float barWidth = 400;
        float barHeight = 22;

        // Fundo cinza
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.9f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Preenchimento (0–1)
        float clamped = Math.min(1f, progressoDistancia);

        // Verde é “bom”, amarelo até metade (feedback suave)
        Color corDistancia = clamped > 0.5f ? Color.GREEN : Color.YELLOW;
        shapeRenderer.setColor(corDistancia);
        shapeRenderer.rect(barX + 2, barY + 2,
            (barWidth - 4) * clamped,
            barHeight - 4);

        shapeRenderer.end();
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
