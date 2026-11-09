package dominio;

import excecao.FonteIndisponivelException;

public interface Sensor {

    String getId();

    String getTipo();

    boolean estaFuncionando();

    double lerValor() throws FonteIndisponivelException;
}
