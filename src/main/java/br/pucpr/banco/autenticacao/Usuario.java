package br.pucpr.banco.autenticacao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Pacote «microsserviço» Autenticação -> banco db-autenticacao (usuários / credenciais).
 * A senha NUNCA é guardada em texto: apenas o hash BCrypt.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Usuario() { // exigido pelo JPA
    }

    public Usuario(String nome, String cpf, String senhaHash) {
        this.nome = nome;
        this.cpf = cpf;
        this.senhaHash = senhaHash;
    }

    @PrePersist
    void antesDeInserir() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getSenhaHash() { return senhaHash; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
