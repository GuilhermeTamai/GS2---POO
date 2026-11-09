package dominio;

import excecao.FonteIndisponivelException;

import java.util.Random;

/**
 * Base para sensores simulados: sorteia um valor dentro de uma faixa realista.
 * As classes filhas só definem id, tipo e faixa.
 */
public abstract class SensorSimulado implements Sensor {

    private final String id;
    private final String tipo;
    private final double minimo;
    private final double maximo;
    private final Random random = new Random();
    private boolean funcionando = true;

    protected SensorSimulado(String id, String tipo, double minimo, double maximo) {
        this.id = id;
        this.tipo = tipo;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTipo() {
        return tipo;
    }

    @Override
    public boolean estaFuncionando() {
        return funcionando;
    }

    /** Permite simular uma falha do sensor (útil no menu e nos testes). */
    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!funcionando) {
            throw new FonteIndisponivelException("Sensor " + id + " (" + tipo + ") está fora de operação.");
        }
        double valor = minimo + random.nextDouble() * (maximo - minimo);
        return Math.round(valor * 100.0) / 100.0;
    }
}
