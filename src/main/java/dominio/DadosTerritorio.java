package dominio;

import excecao.DadosInvalidosException;

/**
 * Dados do território e limiares de chuva (mm/h) que disparam cada nível de alerta.
 * Atributos privados; a única forma de alterar os limiares é por métodos que validam.
 */
public class DadosTerritorio {

    private String regiao;
    private int populacaoExposta;
    private double limiarAtencao;
    private double limiarAlerta;
    private double limiarCritico;

    public DadosTerritorio(String regiao, int populacaoExposta,
                           double limiarAtencao, double limiarAlerta, double limiarCritico)
            throws DadosInvalidosException {
        setRegiao(regiao);
        setPopulacaoExposta(populacaoExposta);
        definirLimiares(limiarAtencao, limiarAlerta, limiarCritico);
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região não pode ser vazia.");
        }
        this.regiao = regiao.trim();
    }

    public int getPopulacaoExposta() {
        return populacaoExposta;
    }

    public void setPopulacaoExposta(int populacaoExposta) throws DadosInvalidosException {
        if (populacaoExposta < 0) {
            throw new DadosInvalidosException("População exposta não pode ser negativa: " + populacaoExposta);
        }
        this.populacaoExposta = populacaoExposta;
    }

    public double getLimiarAtencao() {
        return limiarAtencao;
    }

    public double getLimiarAlerta() {
        return limiarAlerta;
    }

    public double getLimiarCritico() {
        return limiarCritico;
    }

    /** Os três limiares são validados juntos, pois um depende dos outros. */
    public void definirLimiares(double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (atencao <= 0 || alerta <= 0 || critico <= 0) {
            throw new DadosInvalidosException("Os limiares devem ser maiores que zero.");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(
                    "Os limiares devem seguir a ordem atenção < alerta < crítico (recebido: "
                            + atencao + ", " + alerta + ", " + critico + ").");
        }
        this.limiarAtencao = atencao;
        this.limiarAlerta = alerta;
        this.limiarCritico = critico;
    }
}
