package br.insper.loja.partida.dto;

import br.insper.loja.partida.model.Partida;
import br.insper.loja.time.model.Time;

public class RetornarPartidaDTO {
    private Integer id;
    private Time mandante;
    private Time visitante;
    private Integer placarMandante;
    private Integer placarVisitante;
    private String status;

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Time getMandante() {
        return mandante;
    }

    public void setMandante(Time mandante) {
        this.mandante = mandante;
    }

    public Time getVisitante() {
        return visitante;
    }

    public void setVisitante(Time visitante) {
        this.visitante = visitante;
    }

    public Integer getPlacarMandante() {
        return placarMandante;
    }

    public void setPlacarMandante(Integer placarMandante) {
        this.placarMandante = placarMandante;
    }

    public Integer getPlacarVisitante() {
        return placarVisitante;
    }

    public void setPlacarVisitante(Integer placarVisitante) {
        this.placarVisitante = placarVisitante;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Método para criar um DTO a partir de uma Partida
    public static RetornarPartidaDTO getRetornarPartidaDTO(Partida partida) {
        RetornarPartidaDTO dto = new RetornarPartidaDTO();
        dto.setId(partida.getId());
        dto.setMandante(partida.getMandante());
        dto.setVisitante(partida.getVisitante());
        dto.setPlacarMandante(partida.getPlacarMandante());
        dto.setPlacarVisitante(partida.getPlacarVisitante());
        dto.setStatus(partida.getStatus());
        return dto;
    }
}
