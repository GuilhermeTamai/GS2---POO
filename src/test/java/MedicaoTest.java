import dominio.Medicao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MedicaoTest {

    @Test
    void deveCriarMedicaoValida() throws DadosInvalidosException {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act
        Medicao medicao = new Medicao("PLUV-01", "CHUVA", 12.5, agora);

        // Assert
        assertEquals("PLUV-01", medicao.getIdSensor());
        assertEquals(12.5, medicao.getValor());
        assertEquals(agora, medicao.getDataHora());
    }

    @Test
    void deveLancarExcecaoParaChuvaNegativa() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act + Assert
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLUV-01", "CHUVA", -3.0, agora));
        assertTrue(erro.getMessage().contains("negativa"));
    }

    @Test
    void deveLancarExcecaoParaIdSensorVazio() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(" ", "CHUVA", 5.0, LocalDateTime.now()));
    }
}
