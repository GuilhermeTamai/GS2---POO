package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory: concentra a criação das estratégias de previsão.
 * Quem precisa de uma estratégia pede pelo tipo, sem if/else espalhado pelo código.
 * Para adicionar uma estratégia nova basta um novo TipoPrevisao e uma linha aqui.
 */
public class FabricaPrevisao {

    private final Map<TipoPrevisao, Supplier<EstrategiaPrevisao>> criadores = new EnumMap<>(TipoPrevisao.class);

    public FabricaPrevisao(ConfiguracaoSistema configuracao) {
        criadores.put(TipoPrevisao.MEDIA_MOVEL,
                () -> new PrevisaoMediaMovel(configuracao.getJanelaPrevisao()));
        criadores.put(TipoPrevisao.COMPARACAO,
                () -> new PrevisaoComparacao(configuracao.getJanelaPrevisao()));
    }

    public EstrategiaPrevisao criar(TipoPrevisao tipo) throws DadosInvalidosException {
        Supplier<EstrategiaPrevisao> criador = tipo == null ? null : criadores.get(tipo);
        if (criador == null) {
            throw new DadosInvalidosException("Tipo de previsão desconhecido: " + tipo);
        }
        return criador.get();
    }
}
