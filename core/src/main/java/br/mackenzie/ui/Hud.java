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
                       float velocidadeMinima) {

        font.draw(batch, String.format("Tempo: %.1fs", tempo), 40, 700);
        font.draw(batch, String.format("Pedaladas por segundo: %.1f", pedaladasPorSegundo), 40, 660);
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
            font.draw(batch, "⚠ INIMIGO SE APROXIMANDO!", 40, 500);
            font.setColor(Color.WHITE);
        }

        // texto central (NÍVEL X)
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

        // Barra de distância do inimigo (canto inferior direito)
        batch.end(); // pausa o batch para desenhar shapes

        renderBarraDistancia(distanciaInimigo, emPerigo);

        batch.begin(); // retoma o batch
    }

    /**
     * Desenha uma barra visual mostrando a distância do inimigo
     */
    private void renderBarraDistancia(float distancia, boolean perigo) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fundo da barra
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(1080, 40, 180, 30);

        // Barra de distância (quanto mais cheia, mais longe o inimigo)
        float porcentagem = Math.min(1f, distancia / 800f); // 800px = distância "segura"
        Color corBarra = perigo ? Color.RED : Color.GREEN;

        shapeRenderer.setColor(corBarra);
        shapeRenderer.rect(1082, 42, 176 * porcentagem, 26);

        shapeRenderer.end();

        // Texto "INIMIGO"
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);

        // Precisa de um batch ativo para texto
        SpriteBatch tempBatch = new SpriteBatch();
        tempBatch.begin();
        font.draw(tempBatch, "INIMIGO", 1090, 95);
        tempBatch.end();
        tempBatch.dispose();

        font.getData().setScale(2f);
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
