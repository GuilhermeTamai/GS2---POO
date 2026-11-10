import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoMediaMovelTest {

    @Test
    void deveCalcularAMediaDasMedicoes() throws Exception {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5);

        // Act
        double resultado = previsao.prever(HistoricoDeTeste.chuva(10, 20, 30));

        // Assert
        assertEquals(20.0, resultado, 0.0001);
    }

    @Test
    void deveUsarSomenteAsUltimasMedicoesDaJanela() throws Exception {
        // Arrange: a janela de 3 ignora o valor 100, que é o mais antigo
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(3);

        // Act
        double resultado = previsao.prever(HistoricoDeTeste.chuva(100, 10, 20, 30));

        // Assert
        assertEquals(20.0, resultado, 0.0001);
    }

    @Test
    void deveLancarExcecaoQuandoNaoHaMedicoes() {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> previsao.prever(new ArrayList<>()));
    }
}
