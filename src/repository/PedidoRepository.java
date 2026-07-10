package repository;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Pedido;
import model.enums.StatusPedido;

public class PedidoRepository {
    private ArrayList<Pedido> pedidos;

    public PedidoRepository() {
        this.pedidos = new ArrayList<>();
    }

    public void salvar(Pedido pedido) {
        if (pedido == null) {
            JOptionPane.showMessageDialog(null, "Pedido não pode ser nulo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pedido.getStatus() == StatusPedido.FIADO && pedido.getCliente() == null) {
            JOptionPane.showMessageDialog(null, "Pedidos fiados precisam de um cliente associado.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (buscarPorId(pedido.getId()) != null) {
            JOptionPane.showMessageDialog(null, "Pedido com ID " + pedido.getId() + " já existe.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        pedidos.add(pedido);
    }

    public Pedido buscarPorId(int id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId() == id) {
                return pedido;
            }
        }
        return null;
    }

    // atualizar
    // excluir
}
