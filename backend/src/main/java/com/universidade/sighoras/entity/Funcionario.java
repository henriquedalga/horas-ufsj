package com.universidade.sighoras.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "funcionario")
public class Funcionario {
    @Id
    private Long Id;
    private String nome;
    private String email;
    private String senha;
}
