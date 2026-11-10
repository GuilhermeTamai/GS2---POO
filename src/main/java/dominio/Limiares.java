package dominio;

import excecao.DadosInvalidosException;

import java.util.Optional;

/** Três limites (atenção < alerta < crítico) de um tipo de sensor. Imutável e sempre válido. */
public final class Limiares {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiares(double atencao, double alerta, double critico) throws DadosInvalidosException {
        if (atencao <= 0 || alerta <= 0 || critico <= 0) {
            throw new DadosInvalidosException("Os limiares devem ser maiores que zero.");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(
                    "Os limiares devem seguir a ordem atenção < alerta < crítico (recebido: "
                            + atencao + ", " + alerta + ", " + critico + ").");
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }

    /** Nível correspondente ao valor, ou vazio se estiver abaixo do limite de atenção. */
    public Optional<NivelAlerta> classificar(double valor) {
        if (valor >= critico) {
            return Optional.of(NivelAlerta.CRITICO);
        }
        if (valor >= alerta) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor >= atencao) {
            return Optional.of(NivelAlerta.ATENCAO);
        }
        return Optional.empty();
    }

    /** O limite que foi ultrapassado para o nível informado. */
    public double limiteDe(NivelAlerta nivel) {
        return switch (nivel) {
            case ATENCAO -> atencao;
            case ALERTA -> alerta;
            case CRITICO -> critico;
        };
    }
}
