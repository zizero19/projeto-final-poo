package repository;

import java.util.ArrayList;
import java.util.List;

import model.Pedido;
import model.enums.FormaPagamento;
import model.enums.StatusPedido;

public class PedidoRepository {
    private List<Pedido> pedidos;

    public PedidoRepository() {
        this.pedidos = new ArrayList<>();
    }

    public void salvarPedido(Pedido pedido) {
        if (pedido == null) {
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
            if (pedido.getCliente() != null && pedido.getCliente().getCpf().equalsIgnoreCase(cpf)) {
                pedidosEncontrados.add(pedido);
            }
        }

        return pedidosEncontrados;
    }

    public void removerPedido(int id) {
        Pedido pedido = buscarPorId(id);

        if (pedido != null) {
            pedidos.remove(pedido);
        } else {
        }
    }

    public double calcularSaldoDevedor(String cpf) {
        double saldo = 0.0;
        for (Pedido p : buscarPedidosPorCpfDeCliente(cpf)) {
            if (p.getFormaPagamento() == FormaPagamento.FIADO
                    && p.getStatus() != StatusPedido.FINALIZADO
                    && p.getStatus() != StatusPedido.CANCELADO) {
                saldo += p.calcularTotal();
            }
        }
        return saldo;
    }

}