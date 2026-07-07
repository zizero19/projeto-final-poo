package model;

import java.util.List;

public class Cliente extends Pessoa {
    private Turma turmaMatriculada;
    private String telefone;
    private boolean isDevendo;
    private List<Pedido> historicoPedidos;

    public Cliente(Turma turmaMatriculada, String telefone, boolean isDevendo, List<Pedido> historicoPedidos) {
        this.turmaMatriculada = turmaMatriculada;
        this.telefone = telefone;
        this.isDevendo = isDevendo;
        this.historicoPedidos = historicoPedidos;
    }
    public Cliente(String nome, String cpf, String email, Turma turmaMatriculada, String telefone, boolean isDevendo,
            List<Pedido> historicoPedidos) {
        super(nome, cpf, email);
        this.turmaMatriculada = turmaMatriculada;
        this.telefone = telefone;
        this.isDevendo = isDevendo;
        this.historicoPedidos = historicoPedidos;
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
    public boolean isDevendo() {
        return isDevendo;
    }
    public void setDevendo(boolean isDevendo) {
        this.isDevendo = isDevendo;
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
     sb.append("Turma Matriculada: ").append(turmaMatriculada != null ? turmaMatriculada.getNomeTurma() : "Nenhuma").append("\n");
     sb.append("Está devendo: ").append(isDevendo ? "Sim" : "Não").append("\n");
        sb.append("Histórico de Pedidos:\n");
        return sb.toString();

    }

    


    public String visualizarHistoricoPedidos() {
        StringBuilder sb = new StringBuilder();
        sb.append("Histórico de Pedidos:\n");
        for (Pedido pedido : historicoPedidos) {
            sb.append(pedido.toString()).append("\n");
        }
        return sb.toString();
    }




}