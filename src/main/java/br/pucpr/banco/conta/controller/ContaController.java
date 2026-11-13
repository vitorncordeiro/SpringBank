package br.pucpr.banco.conta.controller;

import br.pucpr.banco.conta.dto.AbrirContaRequest;
import br.pucpr.banco.conta.dto.ContaResponse;
import br.pucpr.banco.conta.dto.DepositoRequest;
import br.pucpr.banco.conta.dto.MovimentacaoResponse;
import br.pucpr.banco.conta.dto.SaldoConta;
import br.pucpr.banco.conta.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse abrir(@Valid @RequestBody AbrirContaRequest req) {
        return ContaResponse.de(service.abrir(req.usuarioId(), req.agencia(), req.numero()));
    }

    @GetMapping("/{id}/saldo")
    public SaldoConta saldo(@PathVariable Long id) {
        return service.consultarSaldo(id);
    }

    @PostMapping("/{id}/depositos")
    @ResponseStatus(HttpStatus.CREATED)
    public SaldoConta depositar(@PathVariable Long id, @Valid @RequestBody DepositoRequest req) {
        return service.creditar(id, req.valor(), "DEPOSITO");
    }

    @GetMapping("/{id}/extrato")
    public List<MovimentacaoResponse> extrato(@PathVariable Long id) {
        return service.extrato(id);
    }
}
