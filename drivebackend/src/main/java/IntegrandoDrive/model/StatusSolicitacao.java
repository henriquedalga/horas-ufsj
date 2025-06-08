package IntegrandoDrive.model;

public enum StatusSolicitacao {
    EM_PROCESSAMENTO("Em Processamento"),
    DOCUMENTOS_ENVIADOS("Documentos Enviados"),
    EM_ANALISE("Em Análise"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada"),
    FINALIZADA("Finalizada");
    
    private final String descricao;
    
    StatusSolicitacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}