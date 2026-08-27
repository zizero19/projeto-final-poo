package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Cliente extends Pessoa {
    private Long id;
    private Turma turmaMatriculada;
    private String telefone;
    private BigDecimal saldoDevedor = BigDecimal.ZERO;
    private List<Pedido> historicoPedidos;

    public Cliente() {
    }

    public Cliente(Turma turmaMatriculada, String telefone, BigDecimal saldoDevedor, List<Pedido> historicoPedidos) {
        this.turmaMatriculada = turmaMatriculada;
        this.telefone = telefone;
        setSaldoDevedor(saldoDevedor);
        this.historicoPedidos = historicoPedidos;
    }

    public Cliente(String nome, String cpf, String email, Turma turmaMatriculada, String telefone,
            BigDecimal saldoDevedor,
            List<Pedido> historicoPedidos) {
        super(nome, cpf, email);
        this.turmaMatriculada = turmaMatriculada;
        this.telefone = telefone;
        setSaldoDevedor(saldoDevedor);
        this.historicoPedidos = historicoPedidos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Turma getTurmaMatriculada() {
        return turmaMatriculada;
    }

    public void setTurmaMatriculada(Turma turmaMatriculada) {
        this.turmaMatriculada = turmaMatriculada;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public BigDecimal getSaldoDevedor() {
        return saldoDevedor == null ? BigDecimal.ZERO : saldoDevedor.setScale(2, RoundingMode.HALF_UP);
    }

    public void setSaldoDevedor(BigDecimal saldoDevedor) {
        if (saldoDevedor == null || saldoDevedor.compareTo(BigDecimal.ZERO) < 0) {
            this.saldoDevedor = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            return;
        }
        this.saldoDevedor = saldoDevedor.setScale(2, RoundingMode.HALF_UP);
    }

    public void adicionarDivida(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da dívida deve ser maior que zero.");
        }
        setSaldoDevedor(getSaldoDevedor().add(valor));
    }

    public void registrarPagamento(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser maior que zero.");
        }
        if (valor.compareTo(getSaldoDevedor()) > 0) {
            throw new IllegalArgumentException("O pagamento não pode ser maior que o saldo devedor.");
        }
        setSaldoDevedor(getSaldoDevedor().subtract(valor));
    }

    public List<Pedido> getHistoricoPedidos() {
        return historicoPedidos;
    }

    public void setHistoricoPedidos(List<Pedido> historicoPedidos) {
        this.historicoPedidos = historicoPedidos;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== CLIENTE ========\n");
        sb.append("Nome: ").append(getNome()).append("\n");
        sb.append("CPF: ").append(getCpf()).append("\n");
        sb.append("Email: ").append(getEmail()).append("\n");
        sb.append("Telefone: ").append(telefone).append("\n");
        sb.append("Turma Matriculada: ").append(turmaMatriculada != null ? turmaMatriculada.getNomeTurma() : "Nenhuma")
                .append("\n");
        sb.append("Saldo devedor: R$ ").append(getSaldoDevedor()).append("\n");
        sb.append("Histórico de Pedidos:\n");
        return sb.toString();
    }
}