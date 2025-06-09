package com.universidade.sighoras.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "funcionario")
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "funcionario_seq")
    @SequenceGenerator(name = "funcionario_seq", sequenceName = "funcionario_id_seq", allocationSize = 1)
    private Long id;
    

    @Column(unique = true)
    private String email;
    private String senha;
    
    // Construtor sem o campo ID
    public Funcionario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
