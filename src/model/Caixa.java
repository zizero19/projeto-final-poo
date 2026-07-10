package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.enums.StatusPedido;

public class Caixa {
    private List<Pedido> pedidos;
    private double totalVendas;
    private boolean isAberto;
    private LocalTime abertura;
    private LocalTime fechamento;
    private int qtdMaxClientes;

    public Caixa() {
        this.pedidos = new ArrayList<>();
    }

    public Caixa(int qtdMaxClientes) {
        this.pedidos = new ArrayList<>();
        this.qtdMaxClientes = qtdMaxClientes;
    }

    public Caixa(List<Pedido> pedidos, double totalVendas, boolean isAberto, LocalTime abertura,
            LocalTime fechamento, int qtdMaxClientes) {
        this.pedidos = pedidos;
        this.totalVendas = totalVendas;
        this.isAberto = isAberto;
        this.abertura = abertura;
        this.fechamento = fechamento;
        this.qtdMaxClientes = qtdMaxClientes;
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

    public LocalTime getAbertura() {
        return abertura;
    }

    public void setAbertura(LocalTime abertura) {
        this.abertura = abertura;
    }

    public LocalTime getFechamento() {
        return fechamento;
    }

    public void setFechamento(LocalTime fechamento) {
        this.fechamento = fechamento;
    }

    public int getQtdMaxClientes() {
        return qtdMaxClientes;
    }

    public void setQtdMaxClientes(int qtdMaxClientes) {
        this.qtdMaxClientes = qtdMaxClientes;
    }

   
    public void abrir() {
        if (isAberto) {
            System.out.println("O caixa já está aberto.");
            return;
        }

        this.pedidos = new ArrayList<>();
        this.totalVendas = 0.0;
        this.isAberto = true;
        this.abertura = LocalTime.now();
        this.fechamento = null;
    }

   
    public void fechar() {
        if (!isAberto) {
            System.out.println("O caixa já está fechado.");
            return;
        }

        this.isAberto = false;
        this.fechamento = LocalTime.now();
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

        if (atingiuLimiteDeClientes(pedido)) {
            System.out.println("A quantidade máxima de clientes deste caixa já foi atingida.");
            return;
        }

        pedidos.add(pedido);

        if (pedidoFoiEfetuado(pedido)) {
            totalVendas += pedido.calcularTotal();
        }
    }

    private boolean pedidoFoiEfetuado(Pedido pedido) {
        StatusPedido status = pedido.getStatus();
        return status == StatusPedido.FINALIZADO || status == StatusPedido.FIADO;
    }

    private boolean atingiuLimiteDeClientes(Pedido pedido) {
        if (qtdMaxClientes <= 0) {
            return false;
        }

        Set<Cliente> clientesAtendidos = new HashSet<>();
        for (Pedido p : pedidos) {
            clientesAtendidos.add(p.getCliente());
        }

        boolean clienteJaAtendido = clientesAtendidos.contains(pedido.getCliente());
        return !clienteJaAtendido && clientesAtendidos.size() >= qtdMaxClientes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("===== CAIXA =====\n");
        sb.append("Status: ").append(isAberto ? "Aberto" : "Fechado").append("\n");
        sb.append("Abertura: ").append(abertura != null ? abertura : "-").append("\n");
        sb.append("Fechamento: ").append(fechamento != null ? fechamento : "-").append("\n");
        sb.append("Quantidade máxima de clientes: ").append(qtdMaxClientes).append("\n");
        sb.append("Total de vendas: R$ ").append(String.format("%.2f", totalVendas)).append("\n");
        sb.append("Pedidos registrados: ").append(pedidos == null ? 0 : pedidos.size());

        return sb.toString();
    }
}