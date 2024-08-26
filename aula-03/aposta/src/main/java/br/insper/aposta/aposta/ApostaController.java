package br.insper.aposta.aposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aposta")
public class ApostaController {

    @Autowired
    private ApostaService apostaService;

    @GetMapping
    public List<Aposta> listar(@RequestParam(required = false) String status) {
        if (status != null) {
            return apostaService.buscarPorStatus(status);
        }
        return apostaService.listar();
    }

    @PostMapping
    public void salvar(@RequestBody Aposta aposta) {
        apostaService.salvar(aposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aposta> verificarStatus(@PathVariable String id) {
        try {
            Aposta aposta = apostaService.setStatus(id);
            return ResponseEntity.ok(aposta);
        } catch (ApostaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
