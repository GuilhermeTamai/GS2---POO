package dominio;

import excecao.DadosInvalidosException;

import java.time.LocalDateTime;

public class Medicao {

    private final String idSensor;
    private final String tipo;
    private final double valor;
    private final LocalDateTime dataHora;

    public Medicao(String idSensor, String tipo, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        if (idSensor == null || idSensor.isBlank()) {
            throw new DadosInvalidosException("A medição precisa do id do sensor.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new DadosInvalidosException("A medição precisa do tipo do sensor.");
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadosInvalidosException("Valor de medição inválido (" + tipo + "): " + valor);
        }
        if (valor < 0) {
            throw new DadosInvalidosException("Medição de " + tipo + " negativa: " + valor);
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("A medição precisa de data e hora.");
        }
        this.idSensor = idSensor;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public String getIdSensor() {
        return idSensor;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return dataHora + " | " + idSensor + " (" + tipo + ") = " + valor;
    }
}
