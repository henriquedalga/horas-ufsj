package com.universidade.sighoras.dto;

import com.universidade.sighoras.entity.HoraTipo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoRequestDTO {
    private Long matricula;
    private String nome;
    private HoraTipo horaTipo;
}
