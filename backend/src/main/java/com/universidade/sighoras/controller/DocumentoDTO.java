package com.universidade.sighoras.controller;

public class DocumentoDTO {
    private Long id;
    private String nomeArquivo;
    private String driveUrl;
    private String comentario;
    private String data;        // armazenamos aqui o LocalDateTime.toString()

    public DocumentoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getDriveUrl() { return driveUrl; }
    public void setDriveUrl(String driveUrl) { this.driveUrl = driveUrl; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}