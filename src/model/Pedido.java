package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.enums.FormaPagamento;
import model.enums.StatusPedido;

public class Pedido {
    private Long id;
    private Cliente cliente;
    private List<ItemPedido> itens;
    private LocalDateTime dataHora;
    private StatusPedido status;
    private String observacoes;
    private BigDecimal precoTotal;
    private BigDecimal valorPago;
    private FormaPagamento formaPagamento;

    public Pedido() {
        this.itens = new ArrayList<>();
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.EM_PREPARO;
        this.precoTotal = BigDecimal.ZERO;
        this.valorPago = BigDecimal.ZERO;
    }

    public Pedido(Cliente cliente, String observacoes) {
        this();
        this.cliente = cliente;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal == null ? BigDecimal.ZERO : precoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal == null ? BigDecimal.ZERO : precoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    public void setPrecoTotal(double precoTotal) {
        setPrecoTotal(BigDecimal.valueOf(precoTotal));
    }

    public BigDecimal getValorPago() {
        return valorPago == null ? BigDecimal.ZERO : valorPago.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValorPago(BigDecimal valorPago) {
        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) < 0) {
            this.valorPago = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            return;
        }
        this.valorPago = valorPago.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSaldoDevedor() {
        BigDecimal saldo = getPrecoTotal().subtract(getValorPago());
        return saldo.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public void registrarPagamento(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser maior que zero.");
        }
        if (valor.compareTo(getSaldoDevedor()) > 0) {
            throw new IllegalArgumentException("O pagamento não pode ser maior que o saldo do pedido.");
        }
        setValorPago(getValorPago().add(valor));
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public void adicionarItem(ItemPedido item) {
        if (item == null) {
            System.out.println("O item não pode ser nulo.");
            return;
        }

        itens.add(item);
    }

    public boolean removerItem(ItemPedido item) {
        if (item == null || itens == null) {
            return false;
        }
        return itens.remove(item);
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            if (item != null && item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public void finalizarPedido() {
        if (itens.isEmpty()) {
            System.out.println("Não é possível finalizar um pedido sem itens.");
            return;
        }

        if (status == StatusPedido.FINALIZADO) {
            System.out.println("O pedido já está finalizado.");
            return;
        }

        if (this.status == StatusPedido.AGUARDANDO_PAGAMENTO) {
            this.status = StatusPedido.FINALIZADO;
        }
    }

    public void cancelarPedido() {
        if (this.status == StatusPedido.AGUARDANDO_PAGAMENTO || this.status == StatusPedido.EM_PREPARO) {
            this.status = StatusPedido.CANCELADO;
        } else {
            System.out.println("Não é possível cancelar um pedido que já foi finalizado.");
        }
    }

    public void cobrarPedido() {
        if (this.status == StatusPedido.EM_PREPARO) {
            this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        } else {
            System.out.println("Não é possível cobrar um pedido que já foi finalizado ou cancelado.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("===== PEDIDO =====\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Cliente: ").append(cliente != null ? cliente : "Nenhum").append("\n");
        sb.append("Data/Hora: ").append(dataHora).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Itens do pedido:\n");

        if (itens == null || itens.isEmpty()) {
            sb.append("  - Nenhum item adicionado.\n");
        } else {
            for (int i = 0; i < itens.size(); i++) {
                ItemPedido item = itens.get(i);
                sb.append("  ").append(i + 1).append(". ");
                sb.append(item != null && item.getProduto() != null ? item.getProduto().getNome() : "Item inválido");
                sb.append("\n");
            }
        }

        sb.append("Total: R$ ").append(getPrecoTotal().setScale(2, RoundingMode.HALF_UP));
        return sb.toString();
    }
}
