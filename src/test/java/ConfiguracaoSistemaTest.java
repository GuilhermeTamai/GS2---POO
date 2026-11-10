import excecao.DadosInvalidosException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servico.ConfiguracaoSistema;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguracaoSistemaTest {

    private final ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstance();

    @BeforeEach
    @AfterEach
    void restaurar() {
        configuracao.restaurarPadroes();
    }

    @Test
    void deveRetornarSempreAMesmaInstancia() {
        assertSame(ConfiguracaoSistema.getInstance(), ConfiguracaoSistema.getInstance());
    }

    @Test
    void deveAtualizarLimiaresDeUmTipoDeSensor() throws Exception {
        // Act
        configuracao.definirLimiares("CHUVA", 5, 15, 30);

        // Assert
        assertEquals(15.0, configuracao.getLimiares("CHUVA").getAlerta());
    }

    @Test
    void deveRejeitarTipoDeSensorDesconhecido() {
        assertThrows(DadosInvalidosException.class, () -> configuracao.getLimiares("VENTO"));
        assertThrows(DadosInvalidosException.class, () -> configuracao.definirLimiares("VENTO", 1, 2, 3));
    }

    @Test
    void deveRejeitarJanelaMuitoPequena() {
        assertThrows(DadosInvalidosException.class, () -> configuracao.setJanelaPrevisao(1));
    }
}
