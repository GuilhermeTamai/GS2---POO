import dominio.Medicao;
import excecao.DadosInvalidosException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Apoio dos testes: monta um histórico em ordem cronológica a partir de valores. */
final class HistoricoDeTeste {

    private HistoricoDeTeste() {
    }

    static List<Medicao> criar(String idSensor, String tipo, double... valores) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        for (int i = 0; i < valores.length; i++) {
            lista.add(new Medicao(idSensor, tipo, valores[i], LocalDateTime.of(2026, 11, 8, 10, i, 0)));
        }
        return lista;
    }

    static List<Medicao> chuva(double... valores) throws DadosInvalidosException {
        return criar("PLUV-01", "CHUVA", valores);
    }
}
