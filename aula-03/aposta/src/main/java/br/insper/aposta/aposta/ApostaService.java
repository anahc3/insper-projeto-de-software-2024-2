package br.insper.aposta.aposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApostaService {

    @Autowired
    private ApostaRepository apostaRepository;

    public void salvar(Aposta aposta) {
        aposta.setId(UUID.randomUUID().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPartidaDTO> partida = restTemplate.getForEntity(
                "http://localhost:8080/partida/" + aposta.getIdPartida(),
                RetornarPartidaDTO.class);

        if (partida.getStatusCode().is2xxSuccessful()) {
            apostaRepository.save(aposta);
        }

    }

    public List<Aposta> listar() {
        return apostaRepository.findAll();
    }

    public List<Aposta> buscarPorStatus(String status) {
        return apostaRepository.findByStatus(status);
    }

    public Aposta setStatus(String id) {
        Optional<Aposta> apostaOpt = apostaRepository.findById(id);

        if (apostaOpt.isEmpty()) {
            return null;
        }

        Aposta aposta = apostaOpt.get();
        String status = aposta.getStatus();

        if (status.equals("REALIZADA")) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RetornarPartidaDTO> partidaResponse = restTemplate.getForEntity(
                    "http://localhost:8080/partida/" + aposta.getIdPartida(),
                    RetornarPartidaDTO.class);

            RetornarPartidaDTO partida = partidaResponse.getBody();

            if (partida == null || "NAO_REALIZADA".equals(partida.getStatus())) {
                throw new ApostaNaoEncontradaException("A partida com ID " + aposta.getIdPartida() + " não foi realizada.");
            }

            String resultadoEsperado = aposta.getResultado();
            String novoStatus;

            if (partida.getPlacarMandante() > partida.getPlacarVisitante()) {
                novoStatus = "VITORIA_MANDANTE".equals(resultadoEsperado) ? "GANHOU" : "PERDEU";
            } else if (partida.getPlacarMandante() < partida.getPlacarVisitante()) {
                novoStatus = "VITORIA_VISITANTE".equals(resultadoEsperado) ? "GANHOU" : "PERDEU";
            } else {
                novoStatus = "EMPATE".equals(resultadoEsperado) ? "GANHOU" : "PERDEU";
            }

            aposta.setStatus(novoStatus);
            apostaRepository.save(aposta);
        }

        return aposta;
    }
}
