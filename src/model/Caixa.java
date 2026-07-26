package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.enums.StatusPedido;

public class Caixa {

    private static int PROXIMO_ID = 1;

    private int id;
    private List<Pedido> pedidos;
    private double totalVendas;
    private boolean isAberto;
    private LocalDateTime abertura;
    private LocalDateTime fechamento;

    public Caixa() {
        this.id = PROXIMO_ID++;
        this.pedidos = new ArrayList<>();
        this.totalVendas = 0.0;
        this.isAberto = true;
        this.abertura = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public double getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(double totalVendas) {
        this.totalVendas = totalVendas;
    }

    public boolean isAberto() {
        return isAberto;
    }

    public void setAberto(boolean isAberto) {
        this.isAberto = isAberto;
    }

    public LocalDateTime getAbertura() {
        return abertura;
    }

    public void setAbertura(LocalDateTime abertura) {
        this.abertura = abertura;
    }

    public LocalDateTime getFechamento() {
        return fechamento;
    }

    public void setFechamento(LocalDateTime fechamento) {
        this.fechamento = fechamento;
    }

    public void fechar() {
        this.isAberto = false;
        this.fechamento = LocalDateTime.now();
    }

    public void registrarPedido(Pedido pedido) {
        if (!isAberto) {
            System.out.println("Não é possível registrar pedidos com o caixa fechado.");
            return;
        }

        if (pedido == null) {
            System.out.println("O pedido não pode ser nulo.");
            return;
        }

        pedidos.add(pedido);

        if (pedidoFoiEfetuado(pedido)) {
            totalVendas += pedido.calcularTotal();
        }
    }

    private boolean pedidoFoiEfetuado(Pedido pedido) {
        StatusPedido status = pedido.getStatus();
        return status == StatusPedido.FINALIZADO;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("===== CAIXA =====\n");
        sb.append("Status: ").append(isAberto ? "Aberto" : "Fechado").append("\n");
        sb.append("Abertura: ").append(abertura != null ? abertura : "-").append("\n");
        sb.append("Fechamento: ").append(fechamento != null ? fechamento : "-").append("\n");
        sb.append("Total de vendas: R$ ").append(String.format("%.2f", totalVendas)).append("\n");
        sb.append("Pedidos registrados: ").append(pedidos == null ? 0 : pedidos.size());

        return sb.toString();
    }
}