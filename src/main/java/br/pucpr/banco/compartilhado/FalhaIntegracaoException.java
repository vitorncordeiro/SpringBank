package br.pucpr.banco.compartilhado;

/** Falha na comunicação com o Banco Central (sistema externo). */
public class FalhaIntegracaoException extends RuntimeException {
    public FalhaIntegracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
