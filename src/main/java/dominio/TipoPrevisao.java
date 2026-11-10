package dominio;

public enum TipoPrevisao {
    MEDIA_MOVEL("Média móvel das últimas leituras"),
    COMPARACAO("Comparação com o período anterior");

    private final String descricao;

    TipoPrevisao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
