package persistencia;

import excecao.FonteIndisponivelException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Centraliza a URL do banco H2 e a criação das tabelas.
 *
 * Por padrão usa um arquivo em ./dados, para que o histórico sobreviva
 * ao reiniciar o programa. Nos testes usamos URLs de banco em memória.
 */
public class ConexaoBanco {

    public static final String URL_PADRAO = "jdbc:h2:file:./dados/gs2poo";

    private static final String USUARIO = "sa";
    private static final String SENHA = "";
    private static final String ARQUIVO_SCHEMA = "/schema.sql";

    private final String url;

    public ConexaoBanco(String url) {
        this.url = url;
    }

    public static ConexaoBanco padrao() {
        return new ConexaoBanco(URL_PADRAO);
    }

    /** Abre uma nova conexão. Quem chama deve fechá-la (try-with-resources). */
    public Connection abrir() throws FonteIndisponivelException {
        try {
            return DriverManager.getConnection(url, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Não foi possível conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    /** Cria as tabelas (se ainda não existirem) a partir do schema.sql. */
    public void inicializarSchema() throws FonteIndisponivelException {
        String script = lerScript();
        try (Connection conexao = abrir(); Statement comando = conexao.createStatement()) {
            for (String instrucao : script.split(";")) {
                if (!instrucao.isBlank()) {
                    comando.execute(instrucao.trim());
                }
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao criar as tabelas do banco: " + e.getMessage(), e);
        }
    }

    private String lerScript() throws FonteIndisponivelException {
        try (InputStream entrada = ConexaoBanco.class.getResourceAsStream(ARQUIVO_SCHEMA)) {
            if (entrada == null) {
                throw new FonteIndisponivelException(
                        "Arquivo " + ARQUIVO_SCHEMA + " não encontrado em src/main/resources.");
            }
            String conteudo = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
            // remove as linhas de comentário antes de separar por ';'
            return Arrays.stream(conteudo.split("\\R"))
                    .filter(linha -> !linha.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new FonteIndisponivelException("Erro ao ler o " + ARQUIVO_SCHEMA + ": " + e.getMessage(), e);
        }
    }
}
