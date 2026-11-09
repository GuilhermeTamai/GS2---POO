package dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Ponto do território com risco de alagamento (córrego, bueiro, travessia...). */
public class PontoVulneravel extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();

    public PontoVulneravel(String id, String nome, String regiao) {
        super(id, nome, regiao);
    }

    public void adicionarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    @Override
    public StatusComponente verificarStatus() {
        long ativos = sensores.stream().filter(Sensor::estaFuncionando).count();

        StatusComponente resultado;
        if (sensores.isEmpty() || ativos == 0) {
            resultado = StatusComponente.FORA_DE_OPERACAO;
        } else if (ativos < sensores.size()) {
            resultado = StatusComponente.EM_ATENCAO;
        } else {
            resultado = StatusComponente.OPERANDO;
        }
        setStatus(resultado);
        return resultado;
    }
}
