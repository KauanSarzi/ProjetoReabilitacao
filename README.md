# 🚴 PULSAR - Jogo Sério para Reabilitação Cardiovascular

[![LibGDX](https://img.shields.io/badge/LibGDX-Framework-red.svg)](https://libgdx.com/)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Academic-green.svg)]()

> **Transformando reabilitação cardiovascular em uma experiência gamificada e motivadora através da integração IoT com bicicleta ergométrica.**

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Características Principais](#-características-principais)
- [Contexto Clínico](#-contexto-clínico)
- [Arquitetura Técnica](#-arquitetura-técnica)
- [Sistema de Fases](#-sistema-de-fases)
- [Mecânicas de Jogo](#-mecânicas-de-jogo)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Como Executar](#-como-executar)
- [Roadmap e Melhorias Futuras](#-roadmap-e-melhorias-futuras)
- [Autores](#-autores)

---

## 🎯 Sobre o Projeto

**Pulsar** é um *serious game* desenvolvido como parte do Projeto Final da disciplina de Jogos Digitais da Universidade Presbiteriana Mackenzie. O jogo utiliza a framework **LibGDX** para criar uma experiência de *endless runner* onde as **pedaladas reais** de uma bicicleta ergométrica (capturadas via IoT) controlam diretamente a velocidade e progressão do personagem.

### Problema Abordado

Doenças cardiovasculares são a principal causa de morte no mundo. Programas de reabilitação baseados em bicicleta ergométrica são eficazes, mas enfrentam **baixa adesão** devido a:

- ❌ Monotonia das sessões repetitivas
- ❌ Dificuldade de manter intensidade adequada
- ❌ Falta de feedback imediato sobre o desempenho
- ❌ Baixa percepção de progresso

### Solução Proposta

O Pulsar gamifica o exercício aeróbico através de:

- ✅ **Feedback visual e auditivo em tempo real**
- ✅ **Metas claras e progressivas** (distância + cadência mínima)
- ✅ **Autorregulação da intensidade** via mecânica de perseguição
- ✅ **Métricas clínicas detalhadas** para profissionais de saúde

---

## 🎮 Características Principais

### Sistema de Cadência Dinâmica
- Velocidade do personagem diretamente proporcional às pedaladas por segundo (PPS)
- Sistema de perseguição que incentiva manutenção da intensidade mínima
- Feedback visual de risco quando a cadência cai abaixo do ideal

### Progressão de Fases
- **3 níveis** com dificuldade crescente
- Transições visuais de ambiente (Dia → Entardecer → Noite)
- Requisitos duplos: distância percorrida + cadência média mínima

### HUD Clínico
- Cadência atual e mínima
- Barra de progresso de distância
- Alertas visuais e sonoros de baixa intensidade
- Interface minimalista focada em dados essenciais

### Sistema de Métricas
- Tempo de sessão
- Pedaladas totais
- Cadência média e máxima
- Pontuação baseada em desempenho
- Sistema de classificação (Ciclista Profissional, Pedalador Experiente, etc.)

---

## 🏥 Contexto Clínico

O design do Pulsar segue princípios de reabilitação cardiovascular:

| Princípio Clínico | Implementação no Jogo |
|-------------------|----------------------|
| **Progressão Gradual** | Aumento de 75% na distância e cadência entre níveis |
| **Esforço Contínuo** | Mecânica de perseguição reduz pausas prolongadas |
| **Autorregulação** | Jogador controla intensidade através da cadência |
| **Feedback Corretivo** | Alertas visuais/sonoros indicam intensidade inadequada |
| **Segurança** | Evita picos abruptos; esforço controlado pelo paciente |

---

## 🏗️ Arquitetura Técnica

### Padrões de Projeto Utilizados

```
br.mackenzie/
├── Main.java                 # Game core (ApplicationListener)
├── entities/                 # Entity Component Pattern
│   ├── Player.java          # Componente visual do jogador
│   └── Enemy.java           # Sistema de perseguição
├── screens/                  # Screen Management Pattern
│   ├── MenuScreen.java
│   ├── GameScreen.java      # Loop principal
│   ├── PauseScreen.java
│   └── GameOverScreen.java
├── logic/                    # Game Logic Layer
│   ├── PhaseManager.java    # Controle de progressão
│   └── EnemyManager.java    # IA do inimigo
├── input/
│   └── PedalController.java # Abstração de entrada (IoT/Teclado)
├── ui/
│   └── Hud.java             # Interface clínica
└── data/
    └── GameStats.java       # Armazenamento de métricas
```

### Decisões de Design Técnico

#### 1. **Sistema de Background Parallax**
```java
// Velocidade do background proporcional à cadência
float speedMultiplier = 1f + Math.min(pps / 6f, 20.5f);
bg1x -= bg1speedBase * speedMultiplier * delta;
```

#### 2. **Cálculo de Cadência (Rolling Average)**
```java
// Janela de tempo de 1 segundo para calcular PPS
if (tempoDesdeUltimoReset >= JANELA_TEMPO) {
    pedaladasPorSegundo = pedaladasRecentes / tempoDesdeUltimoReset;
}
```

#### 3. **Sistema de Progressão Inteligente**
```java
// Fase só avança se atingir AMBOS os requisitos
boolean verificarConclusaoFase() {
    return distanciaPercorrida >= fase.distanciaMinima &&
           cadenciaMedia >= fase.cadenciaMinima;
}
```

---

## 📊 Sistema de Fases

| Nível | Distância Mínima | Cadência Mínima | Ambiente | Velocidade do Inimigo |
|-------|------------------|-----------------|----------|----------------------|
| **1** | 50 m | 2.0 ped/s | Dia ☀️ | Base × 0.8 |
| **2** | 100 m | 3.5 ped/s | Entardecer 🌅 | Base × 1.2 |
| **3** | 150 m | 5.0 ped/s | Noite 🌙 | Base × 1.6 |

### Condições de Vitória
- ✅ Completar 150m na Fase 3
- ✅ Manter cadência média ≥ 5.0 ped/s

### Condição de Derrota
- ❌ Ser alcançado pelo inimigo (cadência muito baixa por tempo prolongado)

---

## 🎯 Mecânicas de Jogo

### Sistema de Perseguição Adaptativa

O inimigo funciona como um **indicador visual de risco**, não como obstáculo arbitrário:

```java
if (pedaladasPorSegundo < velocidadeMinima) {
    // Inimigo acelera proporcionalmente ao déficit
    float deficit = velocidadeMinima - pedaladasPorSegundo;
    speedMultiplier = 1f + (deficit * 0.3f);
} else {
    // Inimigo recua lentamente
    enemy.perseguir(delta * -0.3f, playerX);
}
```

### Sistema de Alerta Sonoro

- **Beep periódico** quando a cadência cai abaixo do mínimo
- Intervalo de 1.5 segundos entre alertas
- Som para **imediatamente** ao atingir cadência adequada

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Java** | 17+ | Linguagem principal |
| **LibGDX** | 1.12.1 | Framework de jogo multiplataforma |
| **Gradle** | 8.x | Build automation |
| **Scene2D** | (LibGDX) | Sistema de UI (menus, HUD) |
| **ShapeRenderer** | (LibGDX) | Renderização de barras de progresso |

### Dependências Externas

```gradle
dependencies {
    api "com.badlogicgames.gdx:gdx:$gdxVersion"
    // Assets: Texturas, Sons, Música
}
```

---

## 📁 Estrutura do Projeto

```
pulsar/
├── core/src/main/java/br/mackenzie/
│   ├── Main.java
│   ├── entities/
│   │   ├── Player.java       # 4 frames de animação
│   │   └── Enemy.java        # Sistema de perseguição
│   ├── screens/
│   │   ├── MenuScreen.java   # Menu principal + tela de controles
│   │   ├── GameScreen.java   # Loop principal do jogo
│   │   ├── PauseScreen.java  # Sistema de pausa (ESC)
│   │   └── GameOverScreen.java # Estatísticas finais
│   ├── logic/
│   │   ├── PhaseManager.java     # Controle de fases
│   │   └── EnemyManager.java     # IA do inimigo
│   ├── input/
│   │   └── PedalController.java  # Simulação de IoT (SPACE)
│   ├── ui/
│   │   └── Hud.java              # HUD clínico
│   └── data/
│       └── GameStats.java        # Métricas de sessão
├── assets/
│   ├── images/                   # Texturas e sprites
│   ├── sounds/                   # Efeitos sonoros
│   └── backgroundsom.mp3         # Música ambiente
└── docs/
    └── short_paper_pulsar.pdf    # Documentação acadêmica
```

---

## 🚀 Como Executar

### Pré-requisitos

- **JDK 17** ou superior
- **Gradle 8.x**
- **IDE recomendada:** IntelliJ IDEA ou Eclipse

### Passos

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/ProjetoReabilitacao.git
cd ProjetoReabilitacao
```

2. **Compile o projeto**
```bash
./gradlew build
```

3. **Execute na desktop**
```bash
./gradlew lwjgl3:run
```

### Controles

| Tecla | Ação |
|-------|------|
| **SPACE** | Pedalar (simulação IoT) |
| **ESC** | Pausar jogo |

---

## 🔮 Roadmap e Melhorias Futuras

### Fase 1: Integração IoT Real ⏳
- [ ] Implementar classe `SpacebarTelemetry.java`
- [ ] Integrar sensor de cadência via Bluetooth/WiFi
- [ ] Calibração automática de cadência baseada no perfil do paciente

### Fase 2: Painel Clínico 📊
- [ ] Dashboard web para fisioterapeutas
- [ ] Histórico de sessões e evolução
- [ ] Exportação de relatórios em PDF

### Fase 3: Personalização Adaptativa 🎯
- [ ] Ajuste automático de metas baseado em desempenho
- [ ] Sistema de conquistas (achievements)
- [ ] Multiplayer cooperativo (competição saudável)

### Fase 4: Validação Clínica 🏥
- [ ] Testes com fisioterapeutas
- [ ] Coleta de feedback de pacientes
- [ ] Estudo de caso sobre adesão ao tratamento

---

## 👥 Autores

**Desenvolvido por:**
- Kauan Sarzi
- Guilherme Shinohara
- Ricardo Kawamuro

**Instituição:** Universidade Presbiteriana Mackenzie - Faculdade de Computação e Informática

**Disciplina:** Jogos Digitais (2025-2)

**Professor:** Leandro Pupo Natale

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos. Para uso comercial ou distribuição, entre em contato com os autores.

---

## 📚 Referências

- Balady, G. J. et al. (2007). *Circulation*, 115, 2675–2682.
- Mader, S.; Natkin, S.; Levieux, G. (2012). *IJCSS*, 11(1), 1–13.
- Reis, M. S. et al. (2018). *IEEE TNSRE*, 26(5), 1019–1030.
- Ryan, R. M.; Deci, E. L. (2000). *American Psychologist*, 55, 68–78.

---

<div align="center">

**⭐ Se este projeto foi útil, considere dar uma estrela no GitHub!**

 | [📖 Documentação Completa](Documentos do Projeto/short_paper_pulsar.pdf) | [🐛 Reportar Bug](kauansarzi24@gmail.com)

</div>
