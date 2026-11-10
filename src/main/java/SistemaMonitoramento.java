import dominio.DadosTerritorio;
import dominio.Limiares;
import dominio.Medicao;
import dominio.NivelCorregoSensor;
import dominio.PluviometroSensor;
import dominio.PontoVulneravel;
import dominio.Sensor;
import dominio.SensorSimulado;
import dominio.TipoPrevisao;
import dominio.UmidadeSoloSensor;
import dominio.Alerta;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.ResultadoPrevisao;
import servico.ServicoMonitoramento;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/** Menu principal de console. Captura as exceções e mostra mensagens claras ao usuário. */
public class SistemaMonitoramento {

    private static final Locale LOCAL = Locale.forLanguageTag("pt-BR");

    private final Scanner entrada = new Scanner(System.in);
    private final ServicoMonitoramento servico;
    private final ConfiguracaoSistema configuracao;
    private final DadosTerritorio dados;
    private final PontoVulneravel ponto;
    private final List<SensorSimulado> sensores;

    private SistemaMonitoramento(ServicoMonitoramento servico, ConfiguracaoSistema configuracao,
                                 DadosTerritorio dados, PontoVulneravel ponto, List<SensorSimulado> sensores) {
        this.servico = servico;
        this.configuracao = configuracao;
        this.dados = dados;
        this.ponto = ponto;
        this.sensores = sensores;
    }

    public static void main(String[] args) {
        try {
            ConexaoBanco conexao = ConexaoBanco.padrao();
            conexao.inicializarSchema();
            montar(conexao).executar();
        } catch (FonteIndisponivelException e) {
            System.out.println("Não foi possível iniciar o banco de dados: " + e.getMessage());
        } catch (DadosInvalidosException e) {
            System.out.println("Configuração inicial inválida: " + e.getMessage());
        }
    }

    private static SistemaMonitoramento montar(ConexaoBanco conexao) throws DadosInvalidosException {
        ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstance();

        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 1200, 10, 25, 50);
        configuracao.definirLimiares("CHUVA", dados.getLimiarAtencao(), dados.getLimiarAlerta(), dados.getLimiarCritico());

        PontoVulneravel ponto = new PontoVulneravel("PV-01", "Córrego Central", dados.getRegiao());
        List<SensorSimulado> sensores = new ArrayList<>();
        sensores.add(new PluviometroSensor("PLUV-01"));
        sensores.add(new NivelCorregoSensor("NIV-01"));
        sensores.add(new UmidadeSoloSensor("UMID-01"));
        for (SensorSimulado sensor : sensores) {
            ponto.adicionarSensor(sensor);
        }

        ServicoMonitoramento servico = new ServicoMonitoramento(
                new MedicaoDAO(conexao), new AlertaDAO(conexao), configuracao, new FabricaPrevisao(configuracao));
        return new SistemaMonitoramento(servico, configuracao, dados, ponto, sensores);
    }

    // ---------------------------------------------------------------- menu principal

    private void executar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println();
            System.out.println("=== Monitoramento de Alagamentos - " + ponto.getNome() + " ===");
            System.out.println("1 - Verificar sensores (ler e gravar)");
            System.out.println("2 - Rodar previsões");
            System.out.println("3 - Ver histórico");
            System.out.println("4 - Gerenciar dados do território");
            System.out.println("5 - Status completo");
            System.out.println("0 - Sair");
            int opcao = lerInteiro("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1 -> verificarSensores();
                    case 2 -> rodarPrevisoes();
                    case 3 -> menuHistorico();
                    case 4 -> menuTerritorio();
                    case 5 -> statusCompleto();
                    case 0 -> rodando = false;
                    default -> System.out.println("Opção inexistente.");
                }
            } catch (DadosInvalidosException e) {
                System.out.println("[Dado inválido] " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println("[Fonte indisponível] " + e.getMessage());
            }
        }
        System.out.println("Encerrando. O histórico fica salvo no banco.");
    }

    // ---------------------------------------------------------------- 1. sensores

    private void verificarSensores() {
        for (Sensor sensor : ponto.getSensores()) {
            try {
                Medicao medicao = servico.lerEGravar(sensor);
                System.out.printf(LOCAL, "OK    %s (%s): %.2f gravado.%n",
                        sensor.getId(), sensor.getTipo(), medicao.getValor());
            } catch (FonteIndisponivelException e) {
                System.out.println("FALHA " + sensor.getId() + ": " + e.getMessage());
            } catch (DadosInvalidosException e) {
                System.out.println("FALHA " + sensor.getId() + ": leitura inválida descartada. " + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------- 2. previsões

    private void rodarPrevisoes() {
        TipoPrevisao[] tipos = TipoPrevisao.values();
        System.out.println("Estratégia de previsão:");
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + " - " + tipos[i].getDescricao());
        }
        int escolha = lerInteiro("Escolha: ");
        if (escolha < 1 || escolha > tipos.length) {
            System.out.println("Estratégia inexistente.");
            return;
        }
        TipoPrevisao tipo = tipos[escolha - 1];

        for (Sensor sensor : ponto.getSensores()) {
            try {
                ResultadoPrevisao r = servico.preverEAlertar(sensor.getId(), sensor.getTipo(), tipo);
                System.out.printf(LOCAL, "%s (%s) | %s | previsto: %.2f | leituras usadas: %d%n",
                        r.idSensor(), r.tipoSensor(), r.estrategia(), r.valorPrevisto(), r.leiturasUsadas());
                if (r.temAlerta()) {
                    System.out.println("   >>> " + r.alerta().getNivel() + ": " + r.interpretacao());
                } else {
                    System.out.println("   " + r.interpretacao());
                }
            } catch (DadosInvalidosException e) {
                System.out.println(sensor.getId() + ": previsão não realizada. " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println(sensor.getId() + ": falha de banco. " + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------- 3. histórico

    private void menuHistorico() throws FonteIndisponivelException {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Histórico ---");
            System.out.println("1 - Medições de um sensor");
            System.out.println("2 - Alertas emitidos");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Escolha: ");
            switch (opcao) {
                case 1 -> mostrarMedicoes();
                case 2 -> mostrarAlertas();
                case 0 -> { }
                default -> System.out.println("Opção inexistente.");
            }
        } while (opcao != 0);
    }

    private void mostrarMedicoes() throws FonteIndisponivelException {
        SensorSimulado sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        List<Medicao> medicoes = servico.historico(sensor.getId());
        if (medicoes.isEmpty()) {
            System.out.println("Nenhuma medição gravada para " + sensor.getId() + ".");
            return;
        }
        System.out.println(medicoes.size() + " medição(ões) de " + sensor.getId() + ":");
        for (Medicao medicao : medicoes) {
            System.out.println("  " + medicao);
        }
    }

    private void mostrarAlertas() throws FonteIndisponivelException {
        List<Alerta> alertas = servico.alertasEmitidos();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido até agora.");
            return;
        }
        System.out.println(alertas.size() + " alerta(s), do mais recente para o mais antigo:");
        for (Alerta alerta : alertas) {
            System.out.println("  " + alerta);
        }
    }

    // ---------------------------------------------------------------- 4. território

    private void menuTerritorio() throws DadosInvalidosException {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Dados do território ---");
            System.out.println("1 - Ver dados e limiares");
            System.out.println("2 - Alterar população exposta");
            System.out.println("3 - Alterar região");
            System.out.println("4 - Alterar limiares de um tipo de sensor");
            System.out.println("5 - Simular falha/recuperação de um sensor");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Escolha: ");
            switch (opcao) {
                case 1 -> mostrarDadosTerritorio();
                case 2 -> dados.setPopulacaoExposta(lerInteiro("Nova população exposta: "));
                case 3 -> dados.setRegiao(lerTexto("Nova região: "));
                case 4 -> alterarLimiares();
                case 5 -> alternarFalhaSensor();
                case 0 -> { }
                default -> System.out.println("Opção inexistente.");
            }
        } while (opcao != 0);
    }

    private void mostrarDadosTerritorio() throws DadosInvalidosException {
        System.out.println("Região: " + dados.getRegiao() + " | população exposta: " + dados.getPopulacaoExposta());
        for (String tipo : configuracao.getTiposConhecidos()) {
            Limiares l = configuracao.getLimiares(tipo);
            System.out.printf(LOCAL, "  %-14s atenção %.2f | alerta %.2f | crítico %.2f%n",
                    tipo, l.getAtencao(), l.getAlerta(), l.getCritico());
        }
    }

    private void alterarLimiares() throws DadosInvalidosException {
        List<String> tipos = configuracao.getTiposConhecidos();
        for (int i = 0; i < tipos.size(); i++) {
            System.out.println((i + 1) + " - " + tipos.get(i));
        }
        int escolha = lerInteiro("Tipo de sensor: ");
        if (escolha < 1 || escolha > tipos.size()) {
            System.out.println("Tipo inexistente.");
            return;
        }
        String tipo = tipos.get(escolha - 1);
        double atencao = lerDecimal("Novo limiar de ATENÇÃO: ");
        double alerta = lerDecimal("Novo limiar de ALERTA: ");
        double critico = lerDecimal("Novo limiar CRÍTICO: ");

        configuracao.definirLimiares(tipo, atencao, alerta, critico);
        if ("CHUVA".equals(tipo)) {
            dados.definirLimiares(atencao, alerta, critico); // mantém o DadosTerritorio em sincronia
        }
        System.out.println("Limiares de " + tipo + " atualizados.");
    }

    private void alternarFalhaSensor() {
        SensorSimulado sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        sensor.setFuncionando(!sensor.estaFuncionando());
        System.out.println(sensor.getId() + (sensor.estaFuncionando() ? " voltou a funcionar." : " agora está com falha."));
    }

    // ---------------------------------------------------------------- 5. status

    private void statusCompleto() throws FonteIndisponivelException, DadosInvalidosException {
        System.out.println();
        System.out.println("Ponto: " + ponto.getNome() + " | status: " + ponto.verificarStatus());
        mostrarDadosTerritorio();
        System.out.println("Sensores:");
        for (Sensor sensor : ponto.getSensores()) {
            System.out.println("  " + sensor.getId() + " (" + sensor.getTipo() + ") - "
                    + (sensor.estaFuncionando() ? "funcionando" : "FALHA")
                    + " | leituras no banco: " + servico.contarLeituras(sensor.getId()));
        }
        System.out.println("Alertas emitidos: " + servico.alertasEmitidos().size());
        System.out.println("Mínimo de leituras para prever: " + configuracao.getMinimoLeituras()
                + " | janela da previsão: " + configuracao.getJanelaPrevisao());
    }

    // ---------------------------------------------------------------- entrada

    private SensorSimulado escolherSensor() {
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println((i + 1) + " - " + sensores.get(i).getId() + " (" + sensores.get(i).getTipo() + ")");
        }
        int escolha = lerInteiro("Sensor: ");
        if (escolha < 1 || escolha > sensores.size()) {
            System.out.println("Sensor inexistente.");
            return null;
        }
        return sensores.get(escolha - 1);
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine();
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem).trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido: digite um número inteiro (recebido: \"" + texto + "\").");
            }
        }
    }

    private double lerDecimal(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem).trim().replace(',', '.');
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido: digite um número (recebido: \"" + texto + "\").");
            }
        }
    }
}
