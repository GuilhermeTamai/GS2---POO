package dominio;

import excecao.DadosInvalidosException;

import java.util.List;

/** Previsão = média das últimas N medições do histórico. */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Média móvel");
        if (janela < 1) {
            throw new IllegalArgumentException("A janela da média móvel deve ser no mínimo 1.");
        }
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        if (historico == null || historico.isEmpty()) {
            throw new DadosInvalidosException("Não há medições para calcular a média móvel.");
        }
        int inicio = Math.max(0, historico.size() - janela);
        return calcularMedia(historico.subList(inicio, historico.size()));
    }
}
