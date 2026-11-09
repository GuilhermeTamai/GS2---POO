package dominio;

public abstract class ComponenteTerritorial {

    private final String id;
    private String nome;
    private String regiao;
    private StatusComponente status;

    protected ComponenteTerritorial(String id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = StatusComponente.OPERANDO;
    }

    /** Cada tipo de componente decide como calcula o seu status. */
    public abstract StatusComponente verificarStatus();

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public StatusComponente getStatus() {
        return status;
    }

    protected void setStatus(StatusComponente status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return nome + " [" + regiao + "] - " + status;
    }
}
