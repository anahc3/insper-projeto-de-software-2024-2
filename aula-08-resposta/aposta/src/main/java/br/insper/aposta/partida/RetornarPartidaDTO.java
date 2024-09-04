package br.insper.aposta.partida;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetornarPartidaDTO {
    private String nomeMandante;
    private String nomeVisitante;
    private Integer placarMandante;
    private Integer placarVisitante;
    private String status;

    public boolean isEmpate() {
        return placarMandante != null && placarVisitante != null && placarMandante.equals(placarVisitante);
    }

    public boolean isVitoriaMandante() {
        return placarMandante != null && placarVisitante != null && placarMandante > placarVisitante;
    }

    public boolean isVitoriaVisitante() {
        return placarMandante != null && placarVisitante != null && placarVisitante > placarMandante;
    }
}
