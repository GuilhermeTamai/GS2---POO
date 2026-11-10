import dominio.Alerta;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.PluviometroSensor;
import dominio.Sensor;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.ResultadoPrevisao;
import servico.ServicoMonitoramento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Testa a lógica de alerta sem banco: os DAOs são mockados com Mockito. */
class ServicoMonitoramentoTest {

    private MedicaoDAO medicaoDAO;
    private AlertaDAO alertaDAO;
    private ServicoMonitoramento servico;

    @BeforeEach
    void preparar() {
        ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstance();
        configuracao.restaurarPadroes();
        medicaoDAO = mock(MedicaoDAO.class);
        alertaDAO = mock(AlertaDAO.class);
        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, configuracao, new FabricaPrevisao(configuracao));
    }

    @AfterEach
    void limpar() {
        ConfiguracaoSistema.getInstance().restaurarPadroes();
    }

    @Test
    void deveEmitirAlertaCriticoQuandoAPrevisaoPassaDoLimite() throws Exception {
        // Arrange: média 60 mm/h, acima do limite crítico (50)
        when(medicaoDAO.contarPorSensor("PLUV-01")).thenReturn(5);
        when(medicaoDAO.buscarUltimasPorSensor("PLUV-01", 5))
                .thenReturn(HistoricoDeTeste.chuva(60, 60, 60, 60, 60));

        // Act
        ResultadoPrevisao resultado = servico.preverEAlertar("PLUV-01", "CHUVA", TipoPrevisao.MEDIA_MOVEL);

        // Assert
        assertTrue(resultado.temAlerta());
        assertEquals(60.0, resultado.valorPrevisto(), 0.0001);
        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaDAO).salvar(captor.capture());
        Alerta gravado = captor.getValue();
        assertEquals(NivelAlerta.CRITICO, gravado.getNivel());
        assertEquals(50.0, gravado.getLimite(), 0.0001);
        assertTrue(gravado.getMensagem().contains("PLUV-01"));
    }

    @Test
    void naoDeveEmitirAlertaQuandoAPrevisaoEstaAbaixoDosLimiares() throws Exception {
        // Arrange
        when(medicaoDAO.contarPorSensor("PLUV-01")).thenReturn(5);
        when(medicaoDAO.buscarUltimasPorSensor("PLUV-01", 5))
                .thenReturn(HistoricoDeTeste.chuva(2, 3, 2, 3, 2));

        // Act
        ResultadoPrevisao resultado = servico.preverEAlertar("PLUV-01", "CHUVA", TipoPrevisao.MEDIA_MOVEL);

        // Assert
        assertFalse(resultado.temAlerta());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void naoDevePreverComHistoricoInsuficiente() throws Exception {
        // Arrange: só 2 leituras no banco, o mínimo é 5
        when(medicaoDAO.contarPorSensor("PLUV-01")).thenReturn(2);

        // Act + Assert
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> servico.preverEAlertar("PLUV-01", "CHUVA", TipoPrevisao.MEDIA_MOVEL));
        assertTrue(erro.getMessage().contains("insuficiente"));
        verify(medicaoDAO, never()).buscarUltimasPorSensor(anyString(), anyInt());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void deveGravarALeituraValidaDoSensor() throws Exception {
        // Arrange
        Sensor sensor = mock(Sensor.class);
        when(sensor.getId()).thenReturn("PLUV-01");
        when(sensor.getTipo()).thenReturn("CHUVA");
        when(sensor.lerValor()).thenReturn(12.0);

        // Act
        Medicao medicao = servico.lerEGravar(sensor);

        // Assert
        assertEquals(12.0, medicao.getValor(), 0.0001);
        verify(medicaoDAO).salvar(any(Medicao.class));
    }

    @Test
    void naoDeveGravarLeituraInvalida() throws Exception {
        // Arrange: o sensor devolve chuva negativa
        Sensor sensor = mock(Sensor.class);
        when(sensor.getId()).thenReturn("PLUV-01");
        when(sensor.getTipo()).thenReturn("CHUVA");
        when(sensor.lerValor()).thenReturn(-4.0);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> servico.lerEGravar(sensor));
        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }

    @Test
    void naoDeveGravarQuandoOSensorEstaIndisponivel() throws Exception {
        // Arrange
        PluviometroSensor sensor = new PluviometroSensor("PLUV-01");
        sensor.setFuncionando(false);

        // Act + Assert
        assertThrows(FonteIndisponivelException.class, () -> servico.lerEGravar(sensor));
        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }
}
