package com.universidade.sighoras.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "Arquivos")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Arquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_solicitacao")
    private Long idSolicitacao;

    private String nomeArquivo;
    private String url; //Drive URL
    private String Coment;
    private String status;
    private String data;
    private Long size; // Tamanho do arquivo em bytes

    //construtor para MultipartFile
    public Arquivo(String nomeArquivo, Long idSol, long size, String url) {
        this.nomeArquivo = nomeArquivo;
        this.idSolicitacao = idSol;
        this.url = url; // URL do arquivo no Google Drive, deve ser preenchida após o upload
        this.status = "Pendente"; // Status inicial do arquivo
        this.data = java.time.LocalDate.now().toString(); // Data atual
        this.size = size; // Tamanho do arquivo
    }
}
