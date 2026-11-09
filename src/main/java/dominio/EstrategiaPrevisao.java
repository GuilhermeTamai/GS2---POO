package dominio;

import java.util.List;

/** Esqueleto combinado: a Pessoa C implementa as filhas e a lógica. */
public abstract class EstrategiaPrevisao {

    private final String nome;

    protected EstrategiaPrevisao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /** Recebe o histórico (vindo do banco) e devolve o valor previsto. */
    public abstract double prever(List<Medicao> historico);
}
