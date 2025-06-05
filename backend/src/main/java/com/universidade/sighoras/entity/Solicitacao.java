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
import java.util.List;
import com.universidade.sighoras.entity.Arquivo;

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

    private Long matricula;
    private String nome;
    private String email;
    private HoraTipo horaTipo;
    private String status;
    private String dataSolicitacao;
    private String resposta;
    private String linkPasta;

}