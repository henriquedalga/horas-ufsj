package IntegrandoDrive.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SolicitacaoResponseDTO {
    private Long id;
    private String alunoMatricula;
    private String alunoNome;
    private String status;
    private List<DocumentoDTO> documentos;
    private String comentarios;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    // Constructors
    public SolicitacaoResponseDTO() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAlunoMatricula() { return alunoMatricula; }
    public void setAlunoMatricula(String alunoMatricula) { this.alunoMatricula = alunoMatricula; }
    
    public String getAlunoNome() { return alunoNome; }
    public void setAlunoNome(String alunoNome) { this.alunoNome = alunoNome; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<DocumentoDTO> getDocumentos() { return documentos; }
    public void setDocumentos(List<DocumentoDTO> documentos) { this.documentos = documentos; }
    
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}