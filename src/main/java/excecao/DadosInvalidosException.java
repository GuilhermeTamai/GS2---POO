package excecao;

/**
 * Lançada quando um dado está fora da faixa permitida
 * (medição negativa, população negativa, limiar inválido...).
 */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
