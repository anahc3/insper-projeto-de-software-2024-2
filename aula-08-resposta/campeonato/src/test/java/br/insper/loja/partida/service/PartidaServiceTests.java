package br.insper.loja.partida.service;

import br.insper.loja.partida.dto.EditarPartidaDTO;
import br.insper.loja.partida.dto.RetornarPartidaDTO;
import br.insper.loja.partida.dto.SalvarPartidaDTO;
import br.insper.loja.partida.exception.PartidaNaoEncontradaException;
import br.insper.loja.partida.model.Partida;
import br.insper.loja.partida.repository.PartidaRepository;
import br.insper.loja.time.model.Time;
import br.insper.loja.time.service.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidaServiceTest {

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private TimeService timeService;

    @InjectMocks
    private PartidaService partidaService;

    @Test
    void testCadastrarPartida() {
        // Dados de entrada
        SalvarPartidaDTO salvarPartidaDTO = new SalvarPartidaDTO();
        salvarPartidaDTO.setMandante(1);
        salvarPartidaDTO.setVisitante(2);

        // Dados mockados
        Time mandante = new Time();
        mandante.setIdentificador("mandanteId");
        Time visitante = new Time();
        visitante.setIdentificador("visitanteId");

        Partida partida = new Partida();
        partida.setId(1);
        partida.setMandante(mandante);
        partida.setVisitante(visitante);
        partida.setStatus("AGENDADA");

        when(timeService.getTime(1)).thenReturn(mandante);
        when(timeService.getTime(2)).thenReturn(visitante);
        when(partidaRepository.save(any(Partida.class))).thenReturn(partida);

        // Chamada ao método
        RetornarPartidaDTO resultado = partidaService.cadastrarPartida(salvarPartidaDTO);

        // Verificações
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("mandanteId", resultado.getMandante().getIdentificador());
        assertEquals("visitanteId", resultado.getVisitante().getIdentificador());
        assertEquals("AGENDADA", resultado.getStatus());
    }

    @Test
    void testListarPartidasComMandante() {
        // Dados mockados
        Time mandante = new Time();
        mandante.setIdentificador("mandanteId");
        Partida partida = new Partida();
        partida.setMandante(mandante);
        partida.setStatus("AGENDADA");

        List<Partida> partidas = List.of(partida);
        when(partidaRepository.findAll()).thenReturn(partidas);

        // Chamada ao método
        List<RetornarPartidaDTO> resultado = partidaService.listarPartidas("mandanteId");

        // Verificações
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("mandanteId", resultado.get(0).getMandante().getIdentificador());
    }

    @Test
    void testListarPartidasSemMandante() {
        // Dados mockados
        Partida partida = new Partida();
        partida.setStatus("AGENDADA");

        List<Partida> partidas = List.of(partida);
        when(partidaRepository.findAll()).thenReturn(partidas);

        // Chamada ao método
        List<RetornarPartidaDTO> resultado = partidaService.listarPartidas(null);

        // Verificações
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testEditarPartida() {
        // Dados de entrada
        EditarPartidaDTO editarPartidaDTO = new EditarPartidaDTO();
        editarPartidaDTO.setPlacarMandante(3);
        editarPartidaDTO.setPlacarVisitante(2);

        // Dados mockados
        Partida partida = new Partida();
        partida.setId(1);
        partida.setPlacarMandante(0);
        partida.setPlacarVisitante(0);
        partida.setStatus("AGENDADA");

        Partida partidaAtualizada = new Partida();
        partidaAtualizada.setId(1);
        partidaAtualizada.setPlacarMandante(3);
        partidaAtualizada.setPlacarVisitante(2);
        partidaAtualizada.setStatus("REALIZADA");

        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(partidaRepository.save(any(Partida.class))).thenReturn(partidaAtualizada);

        // Chamada ao método
        RetornarPartidaDTO resultado = partidaService.editarPartida(editarPartidaDTO, 1);

        // Verificações
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(3, resultado.getPlacarMandante());
        assertEquals(2, resultado.getPlacarVisitante());
        assertEquals("REALIZADA", resultado.getStatus());
    }

    @Test
    void testGetPartidaNotFound() {
        Integer id = 1;
        when(partidaRepository.findById(id)).thenReturn(Optional.empty());

        PartidaNaoEncontradaException thrown = assertThrows(
                PartidaNaoEncontradaException.class,
                () -> partidaService.getPartida(id),
                "Expected getPartida() to throw, but it didn't"
        );
        assertEquals("Partida não encontrada", thrown.getMessage());
    }
}
