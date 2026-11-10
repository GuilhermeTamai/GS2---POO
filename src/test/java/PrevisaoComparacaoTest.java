import dominio.PrevisaoComparacao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoComparacaoTest {

    @Test
    void deveProjetarAltaQuandoOsNiveisEstaoSubindo() throws Exception {
        // Arrange: anterior = (10+10)/2 = 10; recente = (20+20+20)/3 = 20; tendência +10
        PrevisaoComparacao previsao = new PrevisaoComparacao(5);

        // Act
        double resultado = previsao.prever(HistoricoDeTeste.chuva(10, 10, 20, 20, 20));

        // Assert
        assertEquals(30.0, resultado, 0.0001);
    }

    @Test
    void naoDeveRetornarValorNegativoQuandoOsNiveisCaemMuito() throws Exception {
        // Arrange: anterior = 20; recente = 2; 2 + (2 - 20) seria negativo
        PrevisaoComparacao previsao = new PrevisaoComparacao(5);

        // Act
        double resultado = previsao.prever(HistoricoDeTeste.chuva(20, 20, 2, 2, 2));

        // Assert
        assertEquals(0.0, resultado, 0.0001);
    }

    @Test
    void deveLancarExcecaoComMenosDeDuasMedicoes() {
        // Arrange
        PrevisaoComparacao previsao = new PrevisaoComparacao(5);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> previsao.prever(HistoricoDeTeste.chuva(10)));
    }
}
