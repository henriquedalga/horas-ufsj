package com.universidade.sighoras.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Table(name = "solicitacao")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long matricula; // Matricula do aluno
    private String nome; // Nome do aluno
    private String email; // Email do aluno
    private HoraTipo horaTipo; 
    private String status; // Status da solicitação (Pendente, Aprovada, Rejeitada)
    private String dataSolicitacao; // Data do envio da solicitação
    private String linkPasta; // Link da pasta do Google Drive

    public void setHoraTipoStr(String horaString) {
        this.horaTipo = HoraTipo.valueOf(horaString);
    }

}