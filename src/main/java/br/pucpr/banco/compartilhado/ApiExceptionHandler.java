package br.pucpr.banco.compartilhado;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** Traduz as exceções para os códigos HTTP usados nos diagramas de sequência. */
@RestControllerAdvice
public class ApiExceptionHandler {

    public record Erro(int status, String erro, String mensagem) {
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Erro> saldoInsuficiente(SaldoInsuficienteException e) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, "Saldo insuficiente", e.getMessage()); // 422
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Erro> naoEncontrado(RecursoNaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, "Não encontrado", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Erro> requisicaoInvalida(IllegalArgumentException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erro> validacao(MethodArgumentNotValidException e) {
        String campos = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos", campos);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Erro> conflito(ObjectOptimisticLockingFailureException e) {
        return resposta(HttpStatus.CONFLICT, "Conflito de concorrência",
                "A conta foi alterada por outra operação. Tente novamente.");
    }

    @ExceptionHandler(FalhaIntegracaoException.class)
    public ResponseEntity<Erro> integracao(FalhaIntegracaoException e) {
        return resposta(HttpStatus.BAD_GATEWAY, "Falha no Banco Central", e.getMessage());
    }

    private ResponseEntity<Erro> resposta(HttpStatus status, String erro, String mensagem) {
        return ResponseEntity.status(status).body(new Erro(status.value(), erro, mensagem));
    }
}
