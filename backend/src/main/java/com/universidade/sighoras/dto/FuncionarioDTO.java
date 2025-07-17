package com.universidade.sighoras.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para transferência de dados de funcionário
    * entre a camada de controle e a camada de serviço.
    */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {
    
    private String senha;
    private String email;
}