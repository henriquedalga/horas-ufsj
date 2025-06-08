package IntegrandoDrive.dto;

import java.time.LocalDateTime;

public class DocumentoDTO {
    private Long id;
    private String nomeOriginal;
    private String driveUrl;
    private Long tamanho;
    private LocalDateTime dataUpload;
    
    // Constructors
    public DocumentoDTO() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNomeOriginal() { return nomeOriginal; }
    public void setNomeOriginal(String nomeOriginal) { this.nomeOriginal = nomeOriginal; }
    
    public String getDriveUrl() { return driveUrl; }
    public void setDriveUrl(String driveUrl) { this.driveUrl = driveUrl; }
    
    public Long getTamanho() { return tamanho; }
    public void setTamanho(Long tamanho) { this.tamanho = tamanho; }
    
    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }
}