package com.universidade.sighoras.entity;

import jakarta.persistence.Entity;
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
    Long id;
    Long idSolicitacao;
    Long matricula;
    String nomeArquivo;
    String link;
    String Comentario;
    String data;
    String Drivelink;
}
