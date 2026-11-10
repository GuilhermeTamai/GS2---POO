package servico;

import dominio.Limiares;
import excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton: uma única configuração compartilhada por todo o sistema
 * (limiares de cada tipo de sensor, janela da previsão e mínimo de leituras).
 * O construtor é privado; o acesso é por getInstance().
 */
public class ConfiguracaoSistema {

    private static final ConfiguracaoSistema INSTANCIA = new ConfiguracaoSistema();

    private final Map<String, Limiares> limiaresPorTipo = new HashMap<>();
    private int janelaPrevisao;
    private int minimoLeituras;

    private ConfiguracaoSistema() {
        restaurarPadroes();
    }

    public static ConfiguracaoSistema getInstance() {
        return INSTANCIA;
    }

    /** Volta aos valores iniciais (também usado pelos testes para isolar o estado). */
    public void restaurarPadroes() {
        try {
            limiaresPorTipo.clear();
            limiaresPorTipo.put("CHUVA", new Limiares(10, 25, 50));              // mm/h
            limiaresPorTipo.put("NIVEL_CORREGO", new Limiares(2.0, 3.0, 4.0));   // metros
            limiaresPorTipo.put("UMIDADE_SOLO", new Limiares(70, 85, 95));       // %
        } catch (DadosInvalidosException e) {
            throw new IllegalStateException("Limiares padrão inválidos no código.", e);
        }
        janelaPrevisao = 5;
        minimoLeituras = 5;
    }

    public int getJanelaPrevisao() {
        return janelaPrevisao;
    }

    public void setJanelaPrevisao(int janela) throws DadosInvalidosException {
        if (janela < 2) {
            throw new DadosInvalidosException("A janela da previsão deve ter pelo menos 2 leituras: " + janela);
        }
        this.janelaPrevisao = janela;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

    public void setMinimoLeituras(int minimo) throws DadosInvalidosException {
        if (minimo < 2) {
            throw new DadosInvalidosException("O mínimo de leituras deve ser pelo menos 2: " + minimo);
        }
        this.minimoLeituras = minimo;
    }

    public Limiares getLimiares(String tipoSensor) throws DadosInvalidosException {
        Limiares limiares = limiaresPorTipo.get(tipoSensor);
        if (limiares == null) {
            throw new DadosInvalidosException("Não há limiares configurados para o tipo de sensor: " + tipoSensor);
        }
        return limiares;
    }

    public void definirLimiares(String tipoSensor, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (!limiaresPorTipo.containsKey(tipoSensor)) {
            throw new DadosInvalidosException("Tipo de sensor desconhecido: " + tipoSensor);
        }
        limiaresPorTipo.put(tipoSensor, new Limiares(atencao, alerta, critico));
    }

    public List<String> getTiposConhecidos() {
        List<String> tipos = new ArrayList<>(limiaresPorTipo.keySet());
        Collections.sort(tipos);
        return tipos;
    }
}
