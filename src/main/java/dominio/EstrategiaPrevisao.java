package dominio;

import excecao.DadosInvalidosException;

import java.util.List;

/**
 * Contrato das estratégias de previsão. Cada filha implementa prever() do seu jeito
 * (polimorfismo) e o serviço usa qualquer uma sem saber qual é.
 */
public abstract class EstrategiaPrevisao {

    private final String nome;

    protected EstrategiaPrevisao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /** Recebe o histórico (vindo do banco, em ordem cronológica) e devolve o valor previsto. */
    public abstract double prever(List<Medicao> historico) throws DadosInvalidosException;

    /** Média simples dos valores. Quem chama garante que a lista não está vazia. */
    protected double calcularMedia(List<Medicao> medicoes) {
        double soma = 0;
        for (Medicao medicao : medicoes) {
            soma += medicao.getValor();
        }
        return soma / medicoes.size();
    }
}
