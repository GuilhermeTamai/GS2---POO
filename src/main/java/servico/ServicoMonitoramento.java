package servico;

import dominio.Alerta;
import dominio.EstrategiaPrevisao;
import dominio.Limiares;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.Sensor;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Regras de negócio: ler sensor, validar e gravar a medição; prever sobre o histórico do banco;
 * comparar a previsão com os limiares e gerar o alerta explicando o porquê.
 */
public class ServicoMonitoramento {

    private static final Locale LOCAL = Locale.forLanguageTag("pt-BR");

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final ConfiguracaoSistema configuracao;
    private final FabricaPrevisao fabrica;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO,
                                ConfiguracaoSistema configuracao, FabricaPrevisao fabrica) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.configuracao = configuracao;
        this.fabrica = fabrica;
    }

    /** Lê o sensor, valida (a Medicao rejeita valor inválido) e grava no banco. */
    public Medicao lerEGravar(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException {
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor, LocalDateTime.now());
        medicaoDAO.salvar(medicao);
        return medicao;
    }

    /**
     * Roda a estratégia sobre o histórico do banco. Com menos leituras do que o mínimo configurado,
     * o sistema "se cala": não prevê e explica o motivo. Se a previsão passa de algum limiar,
     * grava e devolve um alerta que diz qual sensor, qual valor previsto e qual limite foi estourado.
     */
    public ResultadoPrevisao preverEAlertar(String idSensor, String tipoSensor, TipoPrevisao tipoPrevisao)
            throws DadosInvalidosException, FonteIndisponivelException {

        int minimo = configuracao.getMinimoLeituras();
        int disponiveis = medicaoDAO.contarPorSensor(idSensor);
        if (disponiveis < minimo) {
            throw new DadosInvalidosException("Histórico insuficiente para o sensor " + idSensor + ": "
                    + disponiveis + " leitura(s) no banco, mínimo de " + minimo
                    + ". Faça mais leituras antes de prever.");
        }

        Limiares limiares = configuracao.getLimiares(tipoSensor);
        EstrategiaPrevisao estrategia = fabrica.criar(tipoPrevisao);
        List<Medicao> historico = medicaoDAO.buscarUltimasPorSensor(idSensor, configuracao.getJanelaPrevisao());

        double previsto = estrategia.prever(historico);
        Optional<NivelAlerta> nivel = limiares.classificar(previsto);

        if (nivel.isEmpty()) {
            String interpretacao = String.format(LOCAL,
                    "Previsão de %.2f abaixo do limite de atenção (%.2f): sem risco previsto no momento.",
                    previsto, limiares.getAtencao());
            return new ResultadoPrevisao(idSensor, tipoSensor, estrategia.getNome(),
                    previsto, historico.size(), null, interpretacao);
        }

        double limite = limiares.limiteDe(nivel.get());
        String mensagem = String.format(LOCAL,
                "Sensor %s (%s): valor previsto %.2f (%s) atingiu o limite de %s (%.2f). Leituras usadas: %d.",
                idSensor, tipoSensor, previsto, estrategia.getNome(), nivel.get().name(), limite, historico.size());
        Alerta alerta = new Alerta(idSensor, previsto, limite, nivel.get(), mensagem, LocalDateTime.now());
        alertaDAO.salvar(alerta);

        return new ResultadoPrevisao(idSensor, tipoSensor, estrategia.getNome(),
                previsto, historico.size(), alerta, mensagem);
    }

    public List<Medicao> historico(String idSensor) throws FonteIndisponivelException {
        return medicaoDAO.listarPorSensor(idSensor);
    }

    public int contarLeituras(String idSensor) throws FonteIndisponivelException {
        return medicaoDAO.contarPorSensor(idSensor);
    }

    public List<Alerta> alertasEmitidos() throws FonteIndisponivelException {
        return alertaDAO.listarTodos();
    }
}
