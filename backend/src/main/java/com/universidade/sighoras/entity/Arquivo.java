package com.universidade.sighoras.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    
    private Long idSolicitacao;
    private String nomeArquivo;
    private String drivelink;
    private String Comentario;
    private String data;
}
