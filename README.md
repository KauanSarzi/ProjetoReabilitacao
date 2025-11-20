PULSAR: Jogo Sério Gamificado com Integração IoT para Reabilitação Cardiovascular
PULSAR: A Gamified Serious Game with IoT Integration for Cardiovascular Rehabilitation

Autores: [Kauan Sarzi, Guilherme Shinohara, Ricardo Kawamuro]
Instituição: Universidade Presbiteriana Mackenzie – Faculdade de Computação e Informática
E-mails: {kauan, guilherme, ricardo}@mackenzie.br

Abstract

Cardiovascular rehabilitation requires structured aerobic exercise, but adherence is often low due to monotony and lack of feedback. Pulsar is a serious game developed with libGDX and conceptually integrated with IoT sensors on an ergometric bike. Real pedal strokes control an endless runner in which cadence determines speed, challenge, and progression. The system aims to increase motivation, support intensity self-regulation, and provide clinically relevant performance metrics.

Keywords: serious games, cardiac rehabilitation, gamification, ergometric cycling.

Resumo

A reabilitação cardiovascular exige exercícios aeróbicos estruturados, mas a adesão costuma ser baixa pela monotonia e falta de feedback. O Pulsar é um jogo sério desenvolvido em libGDX, integrado conceitualmente a sensores IoT em bicicleta ergométrica. As pedaladas controlam um endless runner no qual a cadência define velocidade, desafio e progressão. O sistema busca aumentar engajamento, apoiar a autorregulação da intensidade e fornecer métricas relevantes aos profissionais.

Palavras-chave: jogos sérios, reabilitação cardiovascular, gamificação, bicicleta ergométrica.

1. Introdução

As doenças cardiovasculares permanecem como a principal causa de morte no mundo. Programas de reabilitação baseados em bicicleta ergométrica reduzem morbimortalidade, mas enfrentam baixa adesão devido à monotonia, dificuldade em manter intensidade adequada e falta de feedback contínuo.

Jogos sérios têm sido explorados como alternativa para tornar exercícios repetitivos mais estimulantes. O Pulsar segue essa abordagem ao transformar pedaladas em elementos de jogo como velocidade, perseguição, metas e feedback visual.

2. Problema e Contexto

Embora a bicicleta ergométrica seja segura e amplamente utilizada, muitos pacientes:

pedalam abaixo da intensidade prescrita;

não percebem progresso ao longo das sessões;

perdem motivação rapidamente.

Esses fatores reduzem a efetividade do treino aeróbico e comprometem a continuidade do tratamento. O Pulsar busca atuar diretamente nesses pontos ao oferecer uma experiência guiada, lúdica e progressiva.

3. Justificativa

O Pulsar utiliza recursos de gamificação para apoiar princípios da reabilitação:

Autorregulação da intensidade: a cadência controla a velocidade do personagem; baixa cadência aproxima o inimigo.

Feedback imediato: HUD com cadência atual, cadência mínima e distância restante.

Metas claras: fases com distâncias e cadências mínimas específicas.

Senso de progresso: tela final com métricas e classificações.

Esse conjunto favorece motivação e consistência – fatores reconhecidos na literatura como essenciais para a adesão.

4. Descrição do Jogo
4.1 Gameplay

O Pulsar é um endless runner 2D controlado por pedaladas reais (simuladas por teclado no protótipo). A velocidade do personagem depende da cadência. Um inimigo persegue o jogador e funciona como alerta visual de baixa intensidade.

4.2 Níveis

O jogo é dividido em três fases com dificuldade crescente:

Nível	Distância	Cadência mínima	Ambiente
1	50 m	2,0 ped/s	Dia
2	100 m	3,5 ped/s	Entardecer
3	150 m	5,0 ped/s	Noite
4.3 HUD

O HUD mostra apenas informações essenciais:

cadência atual;

cadência mínima alvo;

distância percorrida;

barra de progresso;

alerta de baixa intensidade.

4.4 Tela de Resultados

Após cada fase, o jogador recebe:

pedaladas totais;

cadência média e máxima;

tempo total;

nível concluído;

nota e classificação textual.

Esses dados podem ser utilizados por fisioterapeutas para acompanhar evolução.

4.5 Integração IoT

A arquitetura prevê leitura de sensores de cadência conectados à bicicleta. No protótipo atual, a entrada é simulada via teclado, permitindo testes antes da integração completa com hardware.

5. Relação com a Reabilitação Cardiovascular

O design do Pulsar foi guiado por princípios clínicos:

Progressão gradual: níveis mais longos e com maior cadência mínima.

Esforço contínuo: mecânica de perseguição reduz interrupções.

Feedback corretivo: ajustes imediatos baseados na cadência.

Segurança: intensidade autorregulada e sem picos abruptos.

Assim, o Pulsar complementa sessões supervisionadas, sem substituir orientação profissional.

6. Conclusão e Trabalhos Futuros

O Pulsar mostra que jogos sérios podem apoiar o engajamento e o controle de intensidade na reabilitação cardiovascular. Como próximos passos:

implementação da integração IoT real;

personalização automática das metas por perfil de paciente;

testes com profissionais e usuários reais;

criação de painel web para monitoramento clínico.

O protótipo atual demonstra a viabilidade da solução e seu potencial terapêutico.

Referências

Balady, G. J. et al. (2007). Circulation, 115, 2675–2682.
Mader, S., Natkin, S., Levieux, G. (2012). IJCSS, 11(1), 1–13.
Reis, M. S. et al. (2018). IEEE TNSRE, 26(5), 1019–1030.
Ryan, R. M.; Deci, E. L. (2000). American Psychologist, 55, 68–78.
World Health Organization (2021). Cardiovascular Diseases – Fact Sheet.
