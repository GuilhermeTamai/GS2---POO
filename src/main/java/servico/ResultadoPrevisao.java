package servico;

import dominio.Alerta;

/**
 * Resultado de uma previsão: o valor previsto, o que ele significa e, se houve,
 * o alerta emitido (null quando a previsão está abaixo do limite de atenção).
 */
public record ResultadoPrevisao(String idSensor,
                                String tipoSensor,
                                String estrategia,
                                double valorPrevisto,
                                int leiturasUsadas,
                                Alerta alerta,
                                String interpretacao) {

    public boolean temAlerta() {
        return alerta != null;
    }
}
