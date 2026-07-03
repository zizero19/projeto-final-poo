package model;

import java.util.List;

public class Caixa {
    private List<Pedido> pedidos;
    private double totalVendas;
    private boolean isAberto;

    public Caixa() {
    }

    public Caixa(List<Pedido> pedidos, double totalVendas, boolean isAberto) {
        this.pedidos = pedidos;
        this.totalVendas = totalVendas;
        this.isAberto = isAberto;
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

}
