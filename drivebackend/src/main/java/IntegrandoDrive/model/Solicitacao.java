package IntegrandoDrive.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitacoes")
public class Solicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String alunoMatricula;
    
    @Column(nullable = false)
    private String alunoNome;
    
    @Column(name = "pasta_google_drive_id")
    private String pastaGoogleDriveId;
    
    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;
    
    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Documento> documentos = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String comentarios;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    // Constructors
    public Solicitacao() {}
    
    public Solicitacao(String alunoMatricula, String alunoNome) {
        this.alunoMatricula = alunoMatricula;
        this.alunoNome = alunoNome;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusSolicitacao.EM_PROCESSAMENTO;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAlunoMatricula() { return alunoMatricula; }
    public void setAlunoMatricula(String alunoMatricula) { this.alunoMatricula = alunoMatricula; }
    
    public String getAlunoNome() { return alunoNome; }
    public void setAlunoNome(String alunoNome) { this.alunoNome = alunoNome; }
    
    public String getPastaGoogleDriveId() { return pastaGoogleDriveId; }
    public void setPastaGoogleDriveId(String pastaGoogleDriveId) { this.pastaGoogleDriveId = pastaGoogleDriveId; }
    
    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }
    
    public List<Documento> getDocumentos() { return documentos; }
    public void setDocumentos(List<Documento> documentos) { this.documentos = documentos; }
    
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}