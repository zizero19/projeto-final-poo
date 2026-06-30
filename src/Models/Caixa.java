package Models;

import java.util.List;

public class Caixa {
    private List<Pedido> pedidos;
    private Double totalVendas;
    private boolean isAberto;

    public Caixa() {
    }

    public Caixa(List<Pedido> pedidos, Double totalVendas, boolean isAberto) {
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

    public Double getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(Double totalVendas) {
        this.totalVendas = totalVendas;
    }

    public boolean isAberto() {
        return isAberto;
    }

    public void setAberto(boolean isAberto) {
        this.isAberto = isAberto;
    }

}
