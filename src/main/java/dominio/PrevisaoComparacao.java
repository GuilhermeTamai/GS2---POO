package dominio;

import excecao.DadosInvalidosException;

import java.util.List;

/**
 * Compara a média da metade mais recente da janela com a da metade anterior
 * e projeta a mesma variação para a frente (tendência simples).
 * Se o nível está subindo, a previsão fica acima da média atual; se está caindo, abaixo.
 */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoComparacao(int janela) {
        super("Comparação com o período anterior");
        if (janela < 2) {
            throw new IllegalArgumentException("A janela da comparação deve ser no mínimo 2.");
        }
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        if (historico == null || historico.size() < 2) {
            throw new DadosInvalidosException("A comparação precisa de pelo menos 2 medições.");
        }
        int inicio = Math.max(0, historico.size() - janela);
        List<Medicao> recorte = historico.subList(inicio, historico.size());

        int meio = recorte.size() / 2;
        double mediaAnterior = calcularMedia(recorte.subList(0, meio));
        double mediaRecente = calcularMedia(recorte.subList(meio, recorte.size()));

        double tendencia = mediaRecente - mediaAnterior;
        return Math.max(0.0, mediaRecente + tendencia);
    }
}
