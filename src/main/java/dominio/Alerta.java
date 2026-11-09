package dominio;

import java.time.LocalDateTime;

public class Alerta {

    private final String idSensor;
    private final double valorPrevisto;
    private final double limite;
    private final NivelAlerta nivel;
    private final String mensagem;
    private final LocalDateTime dataHora;

    public Alerta(String idSensor, double valorPrevisto, double limite,
                  NivelAlerta nivel, String mensagem, LocalDateTime dataHora) {
        this.idSensor = idSensor;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.nivel = nivel;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    public String getIdSensor() {
        return idSensor;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return dataHora + " | " + nivel + " | " + idSensor + " | " + mensagem;
    }
}
