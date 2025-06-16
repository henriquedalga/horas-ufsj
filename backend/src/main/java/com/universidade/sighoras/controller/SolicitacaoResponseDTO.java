package com.universidade.sighoras.controller;

import com.universidade.sighoras.entity.HoraTipo;
import java.util.List;

public class SolicitacaoResponseDTO {
    private Long id;
    private Long matricula;
    private String nome;
    private HoraTipo horaTipo;
    private String status;
    private String dataSolicitacao;
    private String resposta;
    private String linkPasta;
    private List<DocumentoDTO> arquivos;

    public SolicitacaoResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMatricula() { return matricula; }
    public void setMatricula(Long matricula) { this.matricula = matricula; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public HoraTipo getHoraTipo() { return horaTipo; }
    public void setHoraTipo(HoraTipo horaTipo) { this.horaTipo = horaTipo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(String dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }

    public String getLinkPasta() { return linkPasta; }
    public void setLinkPasta(String linkPasta) { this.linkPasta = linkPasta; }

    public List<DocumentoDTO> getArquivos() { return arquivos; }
    public void setArquivos(List<DocumentoDTO> arquivos) { this.arquivos = arquivos; }
}