package br.insper.aposta.aposta;

import br.insper.aposta.partida.PartidaNaoEncontradaException;
import br.insper.aposta.partida.PartidaNaoRealizadaException;
import br.insper.aposta.partida.PartidaService;
import br.insper.aposta.partida.RetornarPartidaDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ApostaServiceTests {

    @InjectMocks
    ApostaService apostaService;

    @Mock
    ApostaRepository apostaRepository;

    @Mock
    PartidaService partidaService;

    @Test
    public void testSalvarApostaComPartidaEncontrada() {
        Aposta aposta = new Aposta();
        aposta.setIdPartida(1);  // ID da partida deve ser Integer
        aposta.setValor(100.0);

        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setStatus("REALIZADA");
        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(partidaDTO, HttpStatus.OK);

        Mockito.when(partidaService.getPartida(1)).thenReturn(response);
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aposta apostaSalva = apostaService.salvar(aposta);

        Assertions.assertNotNull(apostaSalva);
        Assertions.assertEquals("REALIZADA", apostaSalva.getStatus());
        Assertions.assertNotNull(apostaSalva.getDataAposta());
    }

    @Test
    public void testSalvarApostaComPartidaNaoEncontrada() {
        Aposta aposta = new Aposta();
        aposta.setIdPartida(2);

        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(partidaService.getPartida(2)).thenReturn(response);

        Assertions.assertThrows(PartidaNaoEncontradaException.class,
                () -> apostaService.salvar(aposta));
    }

    @Test
    public void testListarApostas() {
        Mockito.when(apostaRepository.findAll()).thenReturn(List.of(new Aposta(), new Aposta()));

        var apostas = apostaService.listar();

        Assertions.assertNotNull(apostas);
        Assertions.assertEquals(2, apostas.size());
    }

    @Test
    public void testGetApostaQuandoStatusRealizadaEPartidaNaoRealizada() {
        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(4);

        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setStatus("NAO_REALIZADA");
        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(partidaDTO, HttpStatus.OK);

        Mockito.when(apostaRepository.findById("2")).thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(4)).thenReturn(response);

        Assertions.assertThrows(PartidaNaoRealizadaException.class,
                () -> apostaService.getAposta("2"));
    }

    @Test
    public void testGetApostaQuandoApostaNaoEncontrada() {
        Mockito.when(apostaRepository.findById("5")).thenReturn(Optional.empty());

        Assertions.assertThrows(ApostaNaoEncontradaException.class,
                () -> apostaService.getAposta("5"));
    }

    @Test
    public void testGetApostaWhenApostaIsNull() {
        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ApostaNaoEncontradaException.class,
                () -> apostaService.getAposta("1"));
    }

    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizada() {

        Aposta aposta = new Aposta();
        aposta.setStatus("GANHOU");

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Aposta apostaRetorno = apostaService.getAposta("1");
        Assertions.assertNotNull(apostaRetorno);

    }

    @Test
    public void testGetApostaWhenApostaHasStatusPerdida() {
        Aposta aposta = new Aposta();
        aposta.setStatus("PERDIDA");

        Mockito.when(apostaRepository.findById("2"))
                .thenReturn(Optional.of(aposta));

        Aposta apostaRetorno = apostaService.getAposta("2");
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("PERDIDA", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaWhenApostaHasDifferentId() {
        Aposta aposta = new Aposta();
        aposta.setStatus("GANHOU");

        Mockito.when(apostaRepository.findById("4"))
                .thenReturn(Optional.of(aposta));

        Aposta apostaRetorno = apostaService.getAposta("4");
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("GANHOU", apostaRetorno.getStatus());
    }
}
