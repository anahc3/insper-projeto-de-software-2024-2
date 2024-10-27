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
    private ApostaService apostaService;

    @Mock
    private ApostaRepository apostaRepository;

    @Mock
    private PartidaService partidaService;

    private Aposta criarAposta(Integer idPartida, String status, Double valor) {
        Aposta aposta = new Aposta();
        aposta.setIdPartida(idPartida);
        aposta.setStatus(status);
        aposta.setValor(valor);
        return aposta;
    }

    private RetornarPartidaDTO criarPartidaDTO(String status, String nomeMandante, String nomeVisitante, int placarMandante, int placarVisitante) {
        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setStatus(status);
        partidaDTO.setNomeMandante(nomeMandante);
        partidaDTO.setNomeVisitante(nomeVisitante);
        partidaDTO.setPlacarMandante(placarMandante);
        partidaDTO.setPlacarVisitante(placarVisitante);
        return partidaDTO;
    }

    private void mockPartidaServiceResponse(int idPartida, HttpStatus status, RetornarPartidaDTO partidaDTO) {
        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(partidaDTO, status);
        Mockito.when(partidaService.getPartida(idPartida)).thenReturn(response);
    }

    private void mockApostaRepositoryFindById(String id, Optional<Aposta> aposta) {
        Mockito.when(apostaRepository.findById(id)).thenReturn(aposta);
    }

    @Test
    public void testSalvarApostaComPartidaEncontrada() {
        Aposta aposta = criarAposta(1, null, 100.0);
        RetornarPartidaDTO partidaDTO = criarPartidaDTO("REALIZADA", null, null, 0, 0);

        mockPartidaServiceResponse(1, HttpStatus.OK, partidaDTO);
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aposta apostaSalva = apostaService.salvar(aposta);

        Assertions.assertNotNull(apostaSalva);
        Assertions.assertEquals("REALIZADA", apostaSalva.getStatus());
        Assertions.assertNotNull(apostaSalva.getDataAposta());
    }

    @Test
    public void testSalvarApostaComPartidaNaoEncontrada() {
        Aposta aposta = criarAposta(2, null, null);

        mockPartidaServiceResponse(2, HttpStatus.NOT_FOUND, null);

        Assertions.assertThrows(PartidaNaoEncontradaException.class,
                () -> apostaService.salvar(aposta));
    }

    @Test
    public void testListarApostas() {
        Mockito.when(apostaRepository.findAll()).thenReturn(List.of(new Aposta(), new Aposta()));
        List<Aposta> apostas = apostaService.listar();

        Assertions.assertNotNull(apostas);
        Assertions.assertEquals(2, apostas.size());
    }

    @Test
    public void testGetApostaQuandoStatusRealizadaEPartidaNaoRealizada() {
        Aposta aposta = criarAposta(4, "REALIZADA", null);
        RetornarPartidaDTO partidaDTO = criarPartidaDTO("NAO_REALIZADA", null, null, 0, 0);

        mockApostaRepositoryFindById("2", Optional.of(aposta));
        mockPartidaServiceResponse(4, HttpStatus.OK, partidaDTO);

        Assertions.assertThrows(PartidaNaoRealizadaException.class,
                () -> apostaService.getAposta("2"));
    }

    @Test
    public void testGetApostaQuandoApostaNaoEncontrada() {
        mockApostaRepositoryFindById("5", Optional.empty());

        Assertions.assertThrows(ApostaNaoEncontradaException.class,
                () -> apostaService.getAposta("5"));
    }

    @Test
    public void testGetApostaWhenApostaIsNotNull() {
        Aposta aposta = criarAposta(null, "GANHOU", null);

        mockApostaRepositoryFindById("1", Optional.of(aposta));

        Aposta apostaRetorno = apostaService.getAposta("1");

        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("GANHOU", apostaRetorno.getStatus());
    }

    @Test
    public void testSalvarApostaComErroNoServicoDePartida() {
        Aposta aposta = criarAposta(3, null, null);

        mockPartidaServiceResponse(3, HttpStatus.INTERNAL_SERVER_ERROR, null);

        Assertions.assertThrows(PartidaNaoEncontradaException.class,
                () -> apostaService.salvar(aposta));
    }

    @Test
    public void testIsEmpate() {
        RetornarPartidaDTO dto = criarPartidaDTO(null, null, null, 2, 2);
        Assertions.assertTrue(dto.isEmpate());
    }

    @Test
    public void testIsVitoriaMandante() {
        RetornarPartidaDTO dto = criarPartidaDTO(null, null, null, 3, 1);
        Assertions.assertTrue(dto.isVitoriaMandante());
    }

    @Test
    public void testIsVitoriaVisitante() {
        RetornarPartidaDTO dto = criarPartidaDTO(null, null, null, 1, 3);
        Assertions.assertTrue(dto.isVitoriaVisitante());
    }

    @Test
    public void testGetApostaQuandoResultadoEmpateEPartidaEmpate() {
        // Criar uma aposta com o resultado "EMPATE"
        Aposta aposta = new Aposta();
        aposta.setId("1");
        aposta.setStatus("REALIZADA");
        aposta.setResultado("EMPATE");
        aposta.setIdPartida(8);

        // Criar um DTO de partida com status "REALIZADA" e resultado "EMPATE"
        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setNomeMandante("Mandante");
        partidaDTO.setNomeVisitante("Visitante");
        partidaDTO.setPlacarMandante(2);
        partidaDTO.setPlacarVisitante(2);
        partidaDTO.setStatus("REALIZADA");

        // Simular resposta do serviço de partida
        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(partidaDTO, HttpStatus.OK);

        // Mockando o comportamento do repositório e do serviço de partida
        Mockito.when(apostaRepository.findById("1")).thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(8)).thenReturn(response);
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Executar o método de serviço
        Aposta apostaRetorno = apostaService.getAposta("1");

        // Verificar o resultado
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("GANHOU", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaQuandoResultadoVitoriaMandanteEPartidaVitoriaMandante() {
        // Criar uma aposta com o resultado "VITORIA_MANDANTE"
        Aposta aposta = new Aposta();
        aposta.setId("2");
        aposta.setStatus("REALIZADA");
        aposta.setResultado("VITORIA_MANDANTE");
        aposta.setIdPartida(9);

        // Criar um DTO de partida com status "REALIZADA" e resultado "VITORIA_MANDANTE"
        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setNomeMandante("Mandante");
        partidaDTO.setNomeVisitante("Visitante");
        partidaDTO.setPlacarMandante(3);
        partidaDTO.setPlacarVisitante(1);
        partidaDTO.setStatus("REALIZADA");

        // Simular resposta do serviço de partida
        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(partidaDTO, HttpStatus.OK);

        // Mockando o comportamento do repositório e do serviço de partida
        Mockito.when(apostaRepository.findById("2")).thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(9)).thenReturn(response);
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Executar o método de serviço
        Aposta apostaRetorno = apostaService.getAposta("2");

        // Verificar o resultado
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("GANHOU", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaQuandoResultadoEmpateEPartidaVitoriaVisitante() {
        // Criar uma aposta com o resultado "EMPATE"
        Aposta aposta = new Aposta();
        aposta.setId("3");
        aposta.setStatus("REALIZADA");
        aposta.setResultado("EMPATE");
        aposta.setIdPartida(10);

        // Criar um DTO de partida com status "REALIZADA" e resultado "VITORIA_VISITANTE"
        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setNomeMandante("Mandante");
        partidaDTO.setNomeVisitante("Visitante");
        partidaDTO.setPlacarMandante(1);
        partidaDTO.setPlacarVisitante(3);
        partidaDTO.setStatus("REALIZADA");

        // Simular resposta do serviço de partida
        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(partidaDTO, HttpStatus.OK);

        // Mockando o comportamento do repositório e do serviço de partida
        Mockito.when(apostaRepository.findById("3")).thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(10)).thenReturn(response);
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Executar o método de serviço
        Aposta apostaRetorno = apostaService.getAposta("3");

        // Verificar o resultado
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("PERDEU", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaQuandoResultadoNaoCorrespondeEStatusAtualizadoParaPerdeu() {
        // Criar uma aposta com o resultado "EMPATE"
        Aposta aposta = new Aposta();
        aposta.setId("4");
        aposta.setStatus("REALIZADA");
        aposta.setResultado("EMPATE"); // Resultado da aposta que não corresponde ao resultado da partida
        aposta.setIdPartida(11);

        // Criar um DTO de partida com status "REALIZADA" e resultado "VITORIA_VISITANTE"
        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setNomeMandante("Mandante");
        partidaDTO.setNomeVisitante("Visitante");
        partidaDTO.setPlacarMandante(0);
        partidaDTO.setPlacarVisitante(1);
        partidaDTO.setStatus("REALIZADA");

        // Simular resposta do serviço de partida
        ResponseEntity<RetornarPartidaDTO> response = new ResponseEntity<>(partidaDTO, HttpStatus.OK);

        // Mockando o comportamento do repositório e do serviço de partida
        Mockito.when(apostaRepository.findById("4")).thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(11)).thenReturn(response);
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Executar o método de serviço
        Aposta apostaRetorno = apostaService.getAposta("4");

        // Verificar o resultado
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("PERDEU", apostaRetorno.getStatus());
    }
}

