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

Estado atual: domínio, exceções e persistência com JDBC

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
| `ConexaoBanco` (H2) e `schema.sql` | Feito |
| `MedicaoDAO` e `AlertaDAO` com `PreparedStatement` e try-with-resources | Feito |
| Testes dos DAOs: `MedicaoDAOTest`, `AlertaDAOTest` | Feito |
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
    │   ├── persistencia/  ConexaoBanco, MedicaoDAO, AlertaDAO
    │   └── servico/       (serviço, fábrica e configuração)
    ├── main/resources/    schema.sql
    └── test/java/         MedicaoTest, DadosTerritorioTest, PontoVulneravelTest,
                           MedicaoDAOTest, AlertaDAOTest, BancoDeTeste
```

## Como rodar

```
mvn clean test
```

## Banco de dados

- Banco **H2 em arquivo**, criado automaticamente em `./dados/gs2poo` na primeira execução. Por ser arquivo (e não memória), o histórico sobrevive ao reiniciar o programa.
- As tabelas `medicao` e `alerta` são criadas pelo `src/main/resources/schema.sql` (`ConexaoBanco.inicializarSchema()`), que pode rodar a cada inicialização sem apagar dados.
- Para zerar o histórico, apague a pasta `dados/`.
- Todo SQL usa `PreparedStatement`; falhas de banco viram `FonteIndisponivelException` com mensagem clara.
- Os testes dos DAOs usam H2 em memória, sem tocar no arquivo real.

## Decisões da equipe


## Padrão criacional utilizado


## Reflexão crítica


## Divisão de tarefas

| Integrante | Responsabilidade |
|---|---|
| Caio Castelão Carminato | **Previsões, Serviço e Menu Principal** |
| Guilherme Vasques Tamai | **domínio e exceções** |
| Vitor Komura de Freitas | **Persistência JDBC, DAOs e Testes de Banco** |

## Uso de IA

Para essa primeira parte do projeto focada no domínio e nas exceções, utilizamos a IA (Claude) como um apoio inicial para montar o esqueleto das classes e a configuração base do pom.xml. O restante do desenvolvimento dessa etapa, incluindo a lógica de validação, ajuste de IDs e a criação dos testes, foi feito 100% pela equipe para garantir que tudo atenda aos requisitos da GS. Na segunda etapa (Persistência), a IA foi utilizada para gerar o rascunho do schema.sql e a estrutura base da classe de conexão com o banco H2. Em seguida, a equipe assumiu a implementação dos métodos nos DAOs (MedicaoDAO e AlertaDAO), revisando linha por linha para aplicar blocos try-with-resources, garantir o uso de PreparedStatement (evitando injeção de SQL) e mapear corretamente as exceções para atender aos requisitos da avaliação.