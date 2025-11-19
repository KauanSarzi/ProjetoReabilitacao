package br.mackenzie.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PedalController {

    private float tempoDesdeUltimoReset = 0f;
    private final float JANELA_TEMPO = 1f;
    private int pedaladasRecentes = 0;

    private int totalPedaladas = 0;
    private float pedaladasPorSegundo = 0f;

    private float impulso = 0f;
    private final float IMPULSO_DUR = 0.25f;

    public void update(float delta) {
        tempoDesdeUltimoReset += delta;

        // tecla da pedalada
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            impulso = IMPULSO_DUR;
            totalPedaladas++;
            pedaladasRecentes++;
        }

        // gasto do impulso
        if (impulso > 0f) {
            impulso -= delta;
            if (impulso < 0f) impulso = 0f;
        }

        // recalcula pedaladas/s
        if (tempoDesdeUltimoReset >= JANELA_TEMPO) {
            pedaladasPorSegundo = pedaladasRecentes / tempoDesdeUltimoReset;
            pedaladasRecentes = 0;
            tempoDesdeUltimoReset = 0f;
        }
    }

    public boolean isPedaling() {
        return impulso > 0f;
    }

    public float getPedaladasPorSegundo() {
        return pedaladasPorSegundo;
    }

    public int getTotalPedaladas() {
        return totalPedaladas;
    }

    // o mesmo multiplicador que você usou no código original
    public float getSpeedMultiplier() {
        return 1f + pedaladasPorSegundo / 3f;
    }
}
