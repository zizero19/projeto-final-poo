package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.enums.StatusPedido;

public class Pedido {
    private int id;
    private Cliente cliente;
    private List<ItemPedido> itens = new ArrayList<>();
    private LocalDateTime dataHora;
    private StatusPedido status;

    public Pedido() {
    }

    public Pedido(int id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.EM_PREPARO;
        this.itens = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void adicionarItem(ItemPedido item) {
        if (item == null) {
            System.out.println("O item não pode ser nulo.");
            return;
        }

        itens.add(item);
    }

    public void removerItem(ItemPedido item) {
        if (item == null) {
            System.out.println("O item não pode ser nulo.");
            return;
        }

        if (!itens.contains(item)) {
            System.out.println("O item não está presente no pedido.");
            return;
        }

        itens.remove(item);
    }

    public double calcularTotal() {
        double total = 0.0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }

        return total;
    }

    // Public void finalizarPedido() {}
    // Public void cancelarPedido() {}
    // Public void cobrarPedido() {}
    // Public void fiarPedido() {}

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
                sb.append(item != null ? item : "Item inválido");
                sb.append("\n");
            }
        }

        sb.append("Total: R$ ").append(String.format("%.2f", calcularTotal()));
        return sb.toString();
    }
}
