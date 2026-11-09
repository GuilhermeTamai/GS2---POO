import dominio.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PontoVulneravelTest {

    @Test
    void deveEstarEmAtencaoQuandoUmSensorFalha() {
        // Arrange
        PontoVulneravel ponto = new PontoVulneravel("PV-1", "Córrego Central", "Zona Leste");
        PluviometroSensor pluviometro = new PluviometroSensor("PLUV-01");
        NivelCorregoSensor nivel = new NivelCorregoSensor("NIV-01");
        ponto.adicionarSensor(pluviometro);
        ponto.adicionarSensor(nivel);
        nivel.setFuncionando(false);

        // Act
        StatusComponente status = ponto.verificarStatus();

        // Assert
        assertEquals(StatusComponente.EM_ATENCAO, status);
    }
}
