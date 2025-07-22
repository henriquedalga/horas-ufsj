package com.universidade.sighoras.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {
    private Long matricula;
    private String nome;
    private String curso;
    // Adicione outros campos conforme necessário
}