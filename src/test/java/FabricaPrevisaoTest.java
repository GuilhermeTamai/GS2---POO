import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;

import static org.junit.jupiter.api.Assertions.*;

class FabricaPrevisaoTest {

    private final FabricaPrevisao fabrica = new FabricaPrevisao(ConfiguracaoSistema.getInstance());

    @Test
    void deveCriarAEstrategiaPedida() throws Exception {
        // Act
        EstrategiaPrevisao media = fabrica.criar(TipoPrevisao.MEDIA_MOVEL);
        EstrategiaPrevisao comparacao = fabrica.criar(TipoPrevisao.COMPARACAO);

        // Assert
        assertInstanceOf(PrevisaoMediaMovel.class, media);
        assertInstanceOf(PrevisaoComparacao.class, comparacao);
    }

    @Test
    void estrategiasDiferentesDevemSeComportarDeFormaDiferente() throws Exception {
        // Arrange: mesmo histórico para as duas
        EstrategiaPrevisao media = fabrica.criar(TipoPrevisao.MEDIA_MOVEL);
        EstrategiaPrevisao comparacao = fabrica.criar(TipoPrevisao.COMPARACAO);

        // Act
        double previstoMedia = media.prever(HistoricoDeTeste.chuva(10, 10, 20, 20, 20));
        double previstoComparacao = comparacao.prever(HistoricoDeTeste.chuva(10, 10, 20, 20, 20));

        // Assert: média = 16; comparação = 30
        assertEquals(16.0, previstoMedia, 0.0001);
        assertEquals(30.0, previstoComparacao, 0.0001);
    }

    @Test
    void deveLancarExcecaoParaTipoNulo() {
        assertThrows(DadosInvalidosException.class, () -> fabrica.criar(null));
    }
}
