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

O sistema monitora pontos urbanos vulneráveis a alagamentos, coletando dados de sensores simulados de pluviometria, nível de córregos e umidade do solo. A partir do histórico persistido em banco de dados, o sistema aplica estratégias de previsão (como média móvel das medições) para antecipar o risco de transbordamento. Com base nessas previsões e nos limiares de segurança definidos, a aplicação emite alertas graduais (Atenção, Alerta ou Crítico) para prever enchentes antes que ocorram.

### Público beneficiado e decisão apoiada

O projeto beneficia moradores de áreas de risco, associações de bairro e equipes da Defesa Civil. O sistema apoia a tomada de decisão logística e preventiva, permitindo que as autoridades enviem alertas antecipados de evacuação, acionem equipes para limpeza de bueiros obstruídos ou realizem o bloqueio de vias antes que o nível da água atinja um estado crítico, mitigando danos materiais e riscos à vida.

## Tecnologias

- Java 17
- Maven
- JDBC com banco H2
- JUnit 5 e Mockito

## O que já foi adicionado

Estado atual: sistema completo (domínio, exceções, persistência, previsões, alertas, menu e testes)

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
| `EstrategiaPrevisao` (abstrata), `TipoPrevisao` e `Limiares` | Feito |
| Testes: `MedicaoTest`, `DadosTerritorioTest`, `PontoVulneravelTest` | Feito |
| `ConexaoBanco` (H2) e `schema.sql` | Feito |
| `MedicaoDAO` e `AlertaDAO` com `PreparedStatement` e try-with-resources | Feito |
| Testes dos DAOs: `MedicaoDAOTest`, `AlertaDAOTest` | Feito |
| `PrevisaoMediaMovel`, `PrevisaoComparacao` | Feito |
| Padrões criacionais: `FabricaPrevisao` (Factory) e `ConfiguracaoSistema` (Singleton) | Feito |
| `ServicoMonitoramento`, `ResultadoPrevisao` e geração de alertas | Feito |
| Menu principal (`SistemaMonitoramento`) | Feito |
| Testes das estratégias, fábrica, limiares, configuração e serviço (com Mockito) | Feito |

## Estrutura do projeto

```
GS2---POO/
├── pom.xml
├── README.md
└── src/
    ├── main/java/
    │   ├── dominio/       ComponenteTerritorial, PontoVulneravel, Sensor, SensorSimulado,
    │   │                  PluviometroSensor, NivelCorregoSensor, UmidadeSoloSensor,
    │   │                  DadosTerritorio, Medicao, Alerta, NivelAlerta, StatusComponente,
    │   │                  EstrategiaPrevisao, PrevisaoMediaMovel, PrevisaoComparacao,
    │   │                  TipoPrevisao, Limiares
    │   ├── excecao/       DadosInvalidosException, FonteIndisponivelException
    │   ├── persistencia/  ConexaoBanco, MedicaoDAO, AlertaDAO
    │   ├── servico/       ServicoMonitoramento, FabricaPrevisao, ConfiguracaoSistema,
    │   │                  ResultadoPrevisao
    │   └── SistemaMonitoramento.java  (menu principal)
    ├── main/resources/    schema.sql
    └── test/java/         MedicaoTest, DadosTerritorioTest, PontoVulneravelTest,
                           MedicaoDAOTest, AlertaDAOTest, BancoDeTeste, HistoricoDeTeste,
                           PrevisaoMediaMovelTest, PrevisaoComparacaoTest, FabricaPrevisaoTest,
                           LimiaresTest, ConfiguracaoSistemaTest, ServicoMonitoramentoTest
```

## Como rodar

Requisitos: Java 17 e Maven.

```
mvn clean test          # roda todos os testes
mvn compile exec:java   # abre o menu no console
```

Alternativa: importar a pasta como projeto Maven na IDE (Eclipse, IntelliJ ou VS Code), rodar os testes pelo Maven e executar a classe `SistemaMonitoramento` para abrir o menu.

Na primeira execução o banco é criado sozinho em `./dados/gs2poo`. Sugestão para ver o sistema funcionando: use a opção 1 (verificar sensores) pelo menos 5 vezes, depois a opção 2 (rodar previsões) e a opção 3 (ver histórico). A opção 4 permite baixar os limiares para forçar um alerta e simular a falha de um sensor.

## Banco de dados

- Banco **H2 em arquivo**, criado automaticamente em `./dados/gs2poo` na primeira execução. Por ser arquivo (e não memória), o histórico sobrevive ao reiniciar o programa.
- As tabelas `medicao` e `alerta` são criadas pelo `src/main/resources/schema.sql` (`ConexaoBanco.inicializarSchema()`), que pode rodar a cada inicialização sem apagar dados.
- Para zerar o histórico, apague a pasta `dados/`.
- Todo SQL usa `PreparedStatement`; falhas de banco viram `FonteIndisponivelException` com mensagem clara.
- Os testes dos DAOs usam H2 em memória, sem tocar no arquivo real.

## Decisões da equipe

- **Recorte e caminho:** alagamento, com JDBC e H2 (o time domina melhor o DAO com `PreparedStatement` do que o Spring Data).
- **Sensores simulados:** `Random` em faixas realistas (chuva 0 a 120 mm/h, nível do córrego 0 a 5 m, umidade do solo 10 a 100%).
- **Limiares por tipo de sensor:** cada tipo tem os seus (atenção < alerta < crítico), guardados em `ConfiguracaoSistema`. Os limiares de chuva também ficam em `DadosTerritorio`; o menu mantém os dois em sincronia.
- **Previsão sobre o banco:** as duas estratégias recebem as últimas leituras vindas do `MedicaoDAO`, nunca uma lista em memória. A média móvel faz a média da janela; a comparação projeta a diferença entre a metade recente e a anterior da janela.
- **Quando o sistema se cala:** com menos de 5 leituras no banco (valor configurável) a previsão é recusada com mensagem explicando o motivo.
- **Alerta explicado:** a mensagem diz qual sensor, o valor previsto, o método usado e o limite que foi atingido.
- **Erros:** `DadosInvalidosException` nas validações (medição, limiares, população, histórico insuficiente) e `FonteIndisponivelException` para sensor ou banco fora do ar; o menu captura as duas com mensagem clara e nenhum `catch` é vazio.
- **Janela de 5 leituras:** pequena de propósito, para a demonstração funcionar rápido.

## Padrão criacional utilizado

**Factory (`FabricaPrevisao`).** O serviço precisa de uma estratégia de previsão, mas não deve saber qual classe concreta instanciar nem espalhar `if tipo == ...` pelo código. A fábrica recebe um `TipoPrevisao` e devolve a estratégia pronta (já com a janela vinda da configuração). Para criar uma terceira estratégia basta um novo valor no enum e uma linha na fábrica; o serviço e o menu não mudam. Usamos aqui, e não em outro lugar, porque é o único ponto em que a escolha de uma classe depende de uma configuração em tempo de execução.

**Singleton (`ConfiguracaoSistema`).** Os limiares, a janela da previsão e o mínimo de leituras precisam ser os mesmos para todo o sistema: se o menu altera um limiar, o serviço tem que enxergar a mudança. Uma única instância (construtor privado e `getInstance()`) garante essa fonte única. O serviço recebe a configuração pelo construtor, o que permite testá-lo com valores controlados.

## Reflexão crítica

**Quando o sistema deve se calar?** Quando não há base para prever. Com menos de 5 leituras no banco, a média de 2 ou 3 pontos é um chute que parece número exato, então o serviço recusa a previsão e explica quantas leituras faltam. Também não emitimos alerta quando a previsão está abaixo do limite de atenção, para não gerar ruído que faça as pessoas ignorarem os alertas de verdade, e um sensor com falha não gera leitura nenhuma em vez de inventar um valor.

**O que os dados não representam?** Os valores são simulados com `Random`, então não refletem a chuva real. Mesmo com sensores reais, 3 sensores num ponto não dizem nada sobre a rua ao lado, sobre o relevo ou sobre um bueiro entupido. A média móvel também não enxerga uma chuva que começa de repente, porque ela só conhece o passado recente. A população exposta é um número ilustrativo, e o sistema não usa dados pessoais de ninguém.

**Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?** O sistema sabe valores e limites, mas quem conhece o contexto é a equipe no território: se há um evento na rua, se a equipe de limpeza já está lá, qual é o custo de evacuar sem necessidade. Por isso o alerta diz o sensor, o valor previsto, o método e o limite estourado, para a pessoa poder questionar o resultado, e a decisão de evacuar ou bloquear uma via continua humana.

## Divisão de tarefas

| Integrante | Responsabilidade |
|---|---|
| Caio Castelão Carminato | **Previsões, Serviço e Menu Principal** |
| Guilherme Vasques Tamai | **domínio e exceções** |
| Vitor Komura de Freitas | **Persistência JDBC, DAOs e Testes de Banco** |

## Uso de IA

Para essa primeira parte do projeto focada no domínio e nas exceções, utilizamos a IA (Claude) como um apoio inicial para montar o esqueleto das classes e a configuração base do pom.xml. O restante do desenvolvimento dessa etapa, incluindo a lógica de validação, ajuste de IDs e a criação dos testes, foi feito 100% pela equipe para garantir que tudo atenda aos requisitos da GS. Na segunda etapa (Persistência), a IA foi utilizada para gerar o rascunho do schema.sql e a estrutura base da classe de conexão com o banco H2. Em seguida, a equipe assumiu a implementação dos métodos nos DAOs (MedicaoDAO e AlertaDAO), revisando linha por linha para aplicar blocos try-with-resources, garantir o uso de PreparedStatement (evitando injeção de SQL) e mapear corretamente as exceções para atender aos requisitos da avaliação.

Na terceira etapa (previsões, alertas e menu), utilizamos o Claude para nos ajudar com a lógica matemática da estratégia de média móvel e para entender como estruturar o esqueleto do padrão Factory. Após essa geração inicial, a equipe assumiu a integração do ServicoMonitoramento com os DAOs, revisou as chamadas do menu principal (SistemaMonitoramento) garantindo o tratamento correto das exceções (try-catch sem capturas vazias) e escreveu o teste de unidade usando Mockito para validar a regra de negócio isolada do banco. Por fim, revisamos todo o fluxo juntos para garantir que todos dominam o código e conseguirão explicá-lo durante a correção.
