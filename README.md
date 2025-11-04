# 🎮 Projeto – Jogo Reabilitação (IoT + libGDX)

# Guia Completo — Projeto de Reabilitação Cardíaca

## 🫀 O que é o projeto

Você vai criar um **jogo** que ajuda pessoas em **reabilitação cardíaca**.

Em vez de usar um controle, o jogador vai **pedalar** (no início, usando a tecla **ESPAÇO**).

Mais pra frente, essa pedalada será lida de uma **bicicleta real com sensores (IoT)**.

O jogo precisa ter:

- **Menu** (início, pausa, reinício)
- **Um personagem**
- **3 fases** com dificuldade crescente
- **Pontuação** com base na pedalada (ritmo, constância e tempo)

---

## 🚀 Etapas principais

### (A) Planejar

1. **Objetivo clínico**
    - Foco em **ritmo constante**, sem esforço excessivo.
    - Fases vao ter (5 a 10 minutos).
2. **Mecânica**
    - **ESPAÇO = pedalada.**
    - A **frequencia** (quantas vezes por minuto) controla a velocidade do personagem.
    - **Bônus**: manter ritmo constante por alguns segundos.
3. **Fluxo de telas**
    - **Menu** → Iniciar, Como jogar, Sair
    - **Jogo** → HUD com cadência, tempo, pontuação
    - **Pausa** → Retomar, Reiniciar
- **Resultados** → mostra desempenho e evolução

---

### (B) Prototipar sem hardware

### 1. Estrutura do projeto libGDX

core/
com.seuprojeto/
MainGame.java
screens/
MenuScreen.java
GameScreen.java
PauseScreen.java
ResultScreen.java
systems/
InputPedalSystem.java
CadenceAnalyzer.java
ScoringSystem.java
entities/
Player.java
ui/
Hud.java
desktop/

markdown
Copiar código

### 2. Simular pedalada com teclado

- Cada vez que aperta **ESPAÇO**, é uma pedalada.
- Calcule a **cadência (rpm)** contando quantas pedaladas em 5 segundos.
- Faça uma **média móvel** para suavizar.
- Compare a cadência com o **alvo** e premie a **consistência**.

### 3. HUD e Menu

- Mostrar:
    - Cadência atual
    - Cadência alvo
    - Tempo da fase
    - Pontos e bônus

### 4. Fases

| Fase | Alvo (rpm) | Duração | Dificuldade |
| --- | --- | --- | --- |
| 1 | 50 | 60s | Aprender ritmo |
| 2 | 55 | 60s | Obstáculos e ritmo médio |
| 3 | 60 | 90s | Alta constância e eventos extras |

### 5. Pontuação

- Pontos = tempo dentro da zona alvo + bônus de consistência.
- Salvar log em CSV para análise:
tempo, cadência, erro, pontos

---

### (C) Integrar IoT (futuro)

Crie uma **interface genérica** para trocar teclado por sensores reais depois.

```java
public interface PedalInputSource {
  double getCadence();
}
```

**Implementações:**

- `KeyboardPedalInput` → usa tecla ESPAÇO
- `SerialPedalInput` → usa Arduino (porta serial)
- `BLEPedalInput` → usa Bluetooth (relógio, bike smart)

Quando tiver a bike/sensor:

1. Envie a cadência via **serial**.
2. O jogo lê e converte em velocidade.
3. Crie uma tela de **calibração** para ajustar o ritmo.

---

### (D) Entrega e Short Paper

**O que entregar:**

- Projeto no **GitHub** com commits semanais.
- **JAR** executável (desktop).
- **Short Paper** (até 6 páginas) contendo:
    - Problema e justificativa.
    - Descrição do jogo e telas.
    - Explicação da gamificação.
    - Prints e logs.
    - Integração com IoT.

---

## ⚙️ Regras do jogo

| Situação | Efeito |
| --- | --- |
| Cadência muito baixa | personagem anda devagar |
| Dentro da zona ±10 rpm | normal |
| Dentro da zona ±5 rpm por 10s | bônus |
| Ficar 10s abaixo do alvo | reinicia fase |
| ESC | pausa o jogo |

---

## 🧩 Código organizado

- `MainGame`: controla as telas.
- `MenuScreen`: opções principais.
- `GameScreen`: lógica do jogo.
- `ResultScreen`: mostra pontuação.
- `InputPedalSystem`: calcula cadência.
- `CadenceAnalyzer`: suaviza variação.
- `ScoringSystem`: calcula pontos.
- `Hud`: mostra informações.
- `Logger`: salva progresso.

---

## 📊 Como medir consistência

- Pegue os últimos 10s de pedaladas.
- Calcule o **desvio médio** (erro do alvo).
- Se < 5 rpm → ganha **bônus**.
- Mostre ícone ou cor indicando “ritmo ideal”.

---

## 🧠 Progressão de dificuldade

1. Reduzir a margem de erro (±10 → ±5 rpm).
2. Adicionar obstáculos e metas de tempo.
3. Combinar cadência + precisão para vencer desafios.

---

## 🧪 Testes básicos

1. Bata **ESPAÇO** a ~1 vez/segundo (≈60 rpm) → cadência deve aparecer certa.
2. Varie o ritmo → verifique bônus ligar/desligar.
3. Termine as 3 fases → pontuação coerente.
