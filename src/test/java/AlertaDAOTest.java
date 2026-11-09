import dominio.Alerta;
import dominio.NivelAlerta;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistencia.AlertaDAO;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertaDAOTest {

    private AlertaDAO dao;

    @BeforeEach
    void preparar() throws Exception {
        dao = new AlertaDAO(BancoDeTeste.novoEmMemoria());
    }

    private static LocalDateTime hora(int minuto) {
        return LocalDateTime.of(2026, 11, 8, 10, minuto, 0);
    }

    @Test
    void deveGravarELerAlertaPreservandoOsCampos() throws Exception {
        // Arrange
        Alerta alerta = new Alerta("PLUV-01", 62.5, 50.0, NivelAlerta.CRITICO,
                "Chuva prevista de 62.5 mm/h acima do limite crítico de 50.0", hora(0));

        // Act
        dao.salvar(alerta);
        List<Alerta> lidos = dao.listarTodos();

        // Assert
        assertEquals(1, lidos.size());
        Alerta lido = lidos.get(0);
        assertEquals("PLUV-01", lido.getIdSensor());
        assertEquals(62.5, lido.getValorPrevisto());
        assertEquals(50.0, lido.getLimite());
        assertEquals(NivelAlerta.CRITICO, lido.getNivel());
        assertEquals(alerta.getMensagem(), lido.getMensagem());
        assertEquals(hora(0), lido.getDataHora());
    }

    @Test
    void deveListarDoMaisRecenteParaOMaisAntigo() throws Exception {
        // Arrange
        dao.salvar(new Alerta("PLUV-01", 12, 10, NivelAlerta.ATENCAO, "primeiro", hora(0)));
        dao.salvar(new Alerta("PLUV-01", 30, 25, NivelAlerta.ALERTA, "segundo", hora(5)));

        // Act
        List<Alerta> lidos = dao.listarTodos();

        // Assert
        assertEquals("segundo", lidos.get(0).getMensagem());
        assertEquals("primeiro", lidos.get(1).getMensagem());
    }

    @Test
    void deveFiltrarAlertasPorSensor() throws Exception {
        // Arrange
        dao.salvar(new Alerta("PLUV-01", 12, 10, NivelAlerta.ATENCAO, "chuva", hora(0)));
        dao.salvar(new Alerta("NIV-01", 3.5, 3.0, NivelAlerta.ALERTA, "córrego", hora(1)));

        // Act
        List<Alerta> doCorrego = dao.listarPorSensor("NIV-01");

        // Assert
        assertEquals(1, doCorrego.size());
        assertEquals("córrego", doCorrego.get(0).getMensagem());
    }

    @Test
    void deveRejeitarAlertaNulo() {
        assertThrows(DadosInvalidosException.class, () -> dao.salvar(null));
    }
}
