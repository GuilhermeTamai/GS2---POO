# GS2 - POO

Global Solution 2º/2026 - Programação Orientada a Objetos
Tema: **CitizenTech** | Recorte escolhido: **Clima local - chuva intensa e risco de alagamento**

## Integrantes

| Nome | RM |
|---|---|
| Caio Castelão Carminato | RM563630 |
| Guilherme Vasques Tamai | RM563276 |
| Vitor Komura de Freitas | RM563694 |

## Ideia principal do projeto

O sistema monitora pontos urbanos vulneráveis a alagamentos, coletando dados em tempo real de sensores simulados de pluviometria, nível de córregos e umidade do solo. A partir do histórico persistido em banco de dados, o sistema aplica estratégias de previsão (como média móvel das medições) para antecipar o risco de transbordamento. Com base nessas previsões e nos limiares de segurança definidos, a aplicação emite alertas graduais (Atenção, Alerta ou Crítico) para prever enchentes antes que ocorram.

### Público beneficiado e decisão apoiada

O projeto beneficia moradores de áreas de risco, associações de bairro e equipes da Defesa Civil. O sistema apoia a tomada de decisão logística e preventiva, permitindo que as autoridades enviem alertas antecipados de evacuação, acionem equipes para limpeza de bueiros obstruídos ou realizem o bloqueio de vias antes que o nível da água atinja um estado crítico, mitigando danos materiais e riscos à vida.

## Tecnologias

- Java 17
- Maven
- JDBC com banco H2
- JUnit 5 e Mockito

## O que já foi adicionado

Estado atual: esqueleto do domínio e das exceções

| Item | Status |
|---|---|
| `pom.xml` com H2, JUnit 5 e Mockito | Feito |
| `ComponenteTerritorial` (abstrata, com `verificarStatus()`) | Feito |
| `PontoVulneravel` (filha concreta) | Feito |
| Interface `Sensor` e base `SensorSimulado` | Feito |
| `PluviometroSensor`, `NivelCorregoSensor`, `UmidadeSoloSensor` | Feito |
| `DadosTerritorio` (atributos privados e limiares validados) | Feito |
| `Medicao`, `Alerta`, `NivelAlerta`, `StatusComponente` | Feito |
| `DadosInvalidosException` e `FonteIndisponivelException` | Feito |
| `EstrategiaPrevisao` (apenas a assinatura abstrata) | Esqueleto |
| Testes: `MedicaoTest`, `DadosTerritorioTest`, `PontoVulneravelTest` | Feito |
| Persistência com JDBC (`MedicaoDAO`, `AlertaDAO`, `schema.sql`) | A fazer |
| `PrevisaoMediaMovel`, `PrevisaoComparacao` | A fazer |
| Padrão criacional (Factory, Singleton ou Builder) | A fazer |
| `ServicoMonitoramento` e geração de alertas | A fazer |
| Menu principal (`SistemaMonitoramento`) | A fazer |
| Teste com Mockito e demais testes | A fazer |

## Estrutura do projeto

```
GS2 - POO/
├── pom.xml
├── README.md
└── src/
    ├── main/java/
    │   ├── dominio/       ComponenteTerritorial, PontoVulneravel, Sensor, SensorSimulado,
    │   │                  PluviometroSensor, NivelCorregoSensor, UmidadeSoloSensor,
    │   │                  DadosTerritorio, Medicao, Alerta, NivelAlerta, StatusComponente,
    │   │                  EstrategiaPrevisao
    │   ├── excecao/       DadosInvalidosException, FonteIndisponivelException
    │   ├── persistencia/  (DAOs com JDBC)
    │   └── servico/       (serviço, fábrica e configuração)
    └── test/java/         MedicaoTest, DadosTerritorioTest, PontoVulneravelTest
```

## Como rodar

```
mvn clean test
```

## Decisões da equipe


## Padrão criacional utilizado


## Reflexão crítica


## Divisão de tarefas

| Integrante | Responsabilidade |
|---|---|
| Caio Castelão Carminato | |
| Guilherme Vasques Tamai | **domínio e exceções** |
| Vitor Komura de Freitas | |

## Uso de IA

Para essa primeira parte do projeto focada no domínio e nas exceções, utilizamos a IA (Claude) como um apoio inicial para montar o esqueleto das classes e a configuração base do pom.xml. O restante do desenvolvimento dessa etapa, incluindo a lógica de validação, ajuste de IDs e a criação dos testes, foi feito 100% pela equipe para garantir que tudo atenda aos requisitos da GS.