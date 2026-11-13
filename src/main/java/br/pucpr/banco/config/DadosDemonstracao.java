package br.pucpr.banco.config;

import br.pucpr.banco.autenticacao.Usuario;
import br.pucpr.banco.autenticacao.UsuarioRepository;
import br.pucpr.banco.autenticacao.UsuarioService;
import br.pucpr.banco.conta.Conta;
import br.pucpr.banco.conta.ContaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConditionalOnProperty(name = "banco.seed", havingValue = "true", matchIfMissing = true)
public class DadosDemonstracao implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final UsuarioService usuarioService;
    private final ContaService contaService;

    public DadosDemonstracao(UsuarioRepository usuarios, UsuarioService usuarioService, ContaService contaService) {
        this.usuarios = usuarios;
        this.usuarioService = usuarioService;
        this.contaService = contaService;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }
        Usuario ana = usuarioService.cadastrar("Ana Souza", "11111111111", "senha123");
        Usuario bruno = usuarioService.cadastrar("Bruno Lima", "22222222222", "senha456");

        Conta contaAna = contaService.abrir(ana.getId(), "0001", "12345-6");
        Conta contaBruno = contaService.abrir(bruno.getId(), "0001", "65432-1");
        contaService.creditar(contaAna.getId(), new BigDecimal("1000.00"), "DEPOSITO-INICIAL");
        contaService.creditar(contaBruno.getId(), new BigDecimal("500.00"), "DEPOSITO-INICIAL");
    }
}
