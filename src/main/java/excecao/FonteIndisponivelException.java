package excecao;

/**
 * Lançada quando um sensor ou gerador de dados não consegue fornecer a leitura.
 */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public FonteIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
