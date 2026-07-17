package repository;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Pedido;
import model.enums.StatusPedido;

public class PedidoRepository {
    private List<Pedido> pedidos;

    public PedidoRepository() {
        this.pedidos = new ArrayList<>();
    }

    public void salvarPedido(Pedido pedido) {
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

    public List<Pedido> listarPedidos() {
        return pedidos;
    }

    public Pedido buscarPorId(int id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId() == id) {
                return pedido;
            }
        }
        return null;
    }

    public List<Pedido> buscarPedidosPorCpfDeCliente(String cpf) {
        List<Pedido> pedidosEncontrados = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            if (pedido.getCliente().getCpf().equalsIgnoreCase(cpf)) {
                pedidosEncontrados.add(pedido);
            }
        }

        return pedidosEncontrados;
    }

}
