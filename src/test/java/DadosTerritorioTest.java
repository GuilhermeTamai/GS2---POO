import dominio.DadosTerritorio;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DadosTerritorioTest {

    @Test
    void deveAtualizarLimiaresValidos() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 1200, 10, 25, 50);

        // Act
        dados.definirLimiares(15, 30, 60);

        // Assert
        assertEquals(15, dados.getLimiarAtencao());
        assertEquals(60, dados.getLimiarCritico());
    }

    @Test
    void deveRejeitarLimiaresForaDeOrdem() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 1200, 10, 25, 50);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> dados.definirLimiares(40, 25, 50));
        assertEquals(10, dados.getLimiarAtencao()); // não alterou nada
    }

    @Test
    void deveRejeitarPopulacaoNegativa() throws DadosInvalidosException {
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 1200, 10, 25, 50);

        assertThrows(DadosInvalidosException.class, () -> dados.setPopulacaoExposta(-5));
    }
}
