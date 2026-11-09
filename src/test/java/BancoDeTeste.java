import excecao.FonteIndisponivelException;
import persistencia.ConexaoBanco;

import java.util.UUID;

/** Apoio para os testes dos DAOs: cada teste ganha um banco H2 em memória isolado. */
final class BancoDeTeste {

    private BancoDeTeste() {
    }

    static ConexaoBanco novoEmMemoria() throws FonteIndisponivelException {
        ConexaoBanco conexao = new ConexaoBanco("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        conexao.inicializarSchema();
        return conexao;
    }
}
