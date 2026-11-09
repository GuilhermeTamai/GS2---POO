import dominio.Medicao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicaoDAOTest {

    private MedicaoDAO dao;

    @BeforeEach
    void preparar() throws Exception {
        dao = new MedicaoDAO(BancoDeTeste.novoEmMemoria());
    }

    private static LocalDateTime hora(int minuto) {
        return LocalDateTime.of(2026, 11, 8, 10, minuto, 0);
    }

    @Test
    void deveGravarELerMedicao() throws Exception {
        // Arrange
        Medicao medicao = new Medicao("PLUV-01", "CHUVA", 12.5, hora(0));

        // Act
        dao.salvar(medicao);
        List<Medicao> lidas = dao.listarPorSensor("PLUV-01");

        // Assert
        assertEquals(1, lidas.size());
        assertEquals(12.5, lidas.get(0).getValor());
        assertEquals("CHUVA", lidas.get(0).getTipo());
        assertEquals(hora(0), lidas.get(0).getDataHora());
    }

    @Test
    void deveListarSomenteMedicoesDoSensorPedido() throws Exception {
        // Arrange
        dao.salvar(new Medicao("PLUV-01", "CHUVA", 10, hora(0)));
        dao.salvar(new Medicao("NIV-01", "NIVEL_CORREGO", 1.2, hora(1)));

        // Act
        List<Medicao> lidas = dao.listarPorSensor("NIV-01");

        // Assert
        assertEquals(1, lidas.size());
        assertEquals("NIV-01", lidas.get(0).getIdSensor());
    }

    @Test
    void deveBuscarAsUltimasEmOrdemCronologica() throws Exception {
        // Arrange
        for (int i = 0; i < 6; i++) {
            dao.salvar(new Medicao("PLUV-01", "CHUVA", i * 10, hora(i)));
        }

        // Act
        List<Medicao> ultimas = dao.buscarUltimasPorSensor("PLUV-01", 3);

        // Assert: valores 30, 40, 50 (as 3 mais recentes, da mais antiga para a mais nova)
        assertEquals(3, ultimas.size());
        assertEquals(30.0, ultimas.get(0).getValor());
        assertEquals(50.0, ultimas.get(2).getValor());
    }

    @Test
    void deveContarMedicoesPorSensor() throws Exception {
        // Arrange
        dao.salvar(new Medicao("PLUV-01", "CHUVA", 1, hora(0)));
        dao.salvar(new Medicao("PLUV-01", "CHUVA", 2, hora(1)));
        dao.salvar(new Medicao("NIV-01", "NIVEL_CORREGO", 1, hora(2)));

        // Act + Assert
        assertEquals(2, dao.contarPorSensor("PLUV-01"));
        assertEquals(0, dao.contarPorSensor("SENSOR-INEXISTENTE"));
    }

    @Test
    void deveRejeitarQuantidadeInvalidaDeMedicoes() {
        assertThrows(DadosInvalidosException.class, () -> dao.buscarUltimasPorSensor("PLUV-01", 0));
    }

    @Test
    void deveRejeitarMedicaoNula() {
        assertThrows(DadosInvalidosException.class, () -> dao.salvar(null));
    }

    @Test
    void naoDeveExecutarSqlEmbutidoNoIdDoSensor() throws Exception {
        // Arrange: texto malicioso tratado como simples dado pelo PreparedStatement
        String idMalicioso = "X'; DROP TABLE medicao; --";
        dao.salvar(new Medicao(idMalicioso, "CHUVA", 5, hora(0)));

        // Act
        List<Medicao> lidas = dao.listarPorSensor(idMalicioso);

        // Assert: a tabela continua existindo e o texto foi gravado como veio
        assertEquals(1, lidas.size());
        assertEquals(idMalicioso, lidas.get(0).getIdSensor());
    }

    @Test
    void dadosDevemSobreviverAoReiniciar(@TempDir Path pasta) throws Exception {
        // Arrange: banco em arquivo
        String url = "jdbc:h2:file:" + pasta.resolve("banco").toAbsolutePath().toString().replace('\\', '/');
        ConexaoBanco primeira = new ConexaoBanco(url);
        primeira.inicializarSchema();
        new MedicaoDAO(primeira).salvar(new Medicao("PLUV-01", "CHUVA", 7.5, hora(0)));

        // Act: "reinicia" abrindo uma nova conexão ao mesmo arquivo
        ConexaoBanco segunda = new ConexaoBanco(url);
        segunda.inicializarSchema();
        List<Medicao> lidas = new MedicaoDAO(segunda).listarPorSensor("PLUV-01");

        // Assert
        assertEquals(1, lidas.size());
        assertEquals(7.5, lidas.get(0).getValor());
    }
}
