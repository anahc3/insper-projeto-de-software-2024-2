package br.insper.aposta.aposta;

public class ApostaNaoEncontradaException extends RuntimeException {
    public ApostaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
