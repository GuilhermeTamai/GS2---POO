import dominio.Limiares;
import dominio.NivelAlerta;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimiaresTest {

    @Test
    void deveClassificarCadaFaixa() throws Exception {
        // Arrange
        Limiares limiares = new Limiares(10, 25, 50);

        // Act + Assert
        assertTrue(limiares.classificar(9.99).isEmpty());
        assertEquals(NivelAlerta.ATENCAO, limiares.classificar(10).orElseThrow());
        assertEquals(NivelAlerta.ALERTA, limiares.classificar(30).orElseThrow());
        assertEquals(NivelAlerta.CRITICO, limiares.classificar(50).orElseThrow());
    }

    @Test
    void deveInformarOLimiteDoNivel() throws Exception {
        // Arrange
        Limiares limiares = new Limiares(10, 25, 50);

        // Act + Assert
        assertEquals(25.0, limiares.limiteDe(NivelAlerta.ALERTA));
    }

    @Test
    void deveRejeitarLimiaresForaDeOrdem() {
        assertThrows(DadosInvalidosException.class, () -> new Limiares(30, 20, 50));
    }
}
