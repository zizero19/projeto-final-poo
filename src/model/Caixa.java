package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.enums.StatusPedido;

public class Caixa {
    private Long id;
    private List<Pedido> pedidos;
    private BigDecimal totalVendas;
    private boolean isAberto;
    private LocalDateTime abertura;
    private LocalDateTime fechamento;

    public Caixa() {
        this.pedidos = new ArrayList<>();
        this.totalVendas = BigDecimal.ZERO;
        this.isAberto = true;
        this.abertura = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public BigDecimal getTotalVendas() {
        return totalVendas == null ? BigDecimal.ZERO : totalVendas.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTotalVendas(BigDecimal totalVendas) {
        this.totalVendas = totalVendas == null ? BigDecimal.ZERO : totalVendas.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTotalVendas(double totalVendas) {
        setTotalVendas(BigDecimal.valueOf(totalVendas));
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
            totalVendas = totalVendas.add(pedido.calcularTotal());
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
        sb.append("Total de vendas: R$ ").append(getTotalVendas().setScale(2, RoundingMode.HALF_UP)).append("\n");
        sb.append("Pedidos registrados: ").append(pedidos == null ? 0 : pedidos.size());

        return sb.toString();
    }
}