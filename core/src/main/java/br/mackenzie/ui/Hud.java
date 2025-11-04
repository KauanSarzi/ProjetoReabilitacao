package br.mackenzie.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hud {

    private final BitmapFont font;
    private final GlyphLayout layout;

    public Hud() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch,
                       float tempo,
                       float pedaladasPorSegundo,
                       float pontos,
                       int totalPedaladas,
                       String levelText,
                       float levelTextTimer) {

        font.draw(batch, String.format("Tempo: %.1fs", tempo), 40, 700);
        font.draw(batch, String.format("Pedaladas por segundo: %.1f", pedaladasPorSegundo), 40, 660);
        font.draw(batch, String.format("Pontos: %.0f", pontos), 40, 620);
        font.draw(batch, String.format("Pedaladas totais: %d", totalPedaladas), 40, 580);

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
    }

    public void dispose() {
        font.dispose();
    }
}
