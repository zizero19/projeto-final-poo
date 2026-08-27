package repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import infrastructure.database.ConnectionFactory;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.enums.FormaPagamento;
import model.enums.StatusPedido;

public class PedidoRepository {
    private final ClienteRepository clienteRepository = new ClienteRepository();
    private final ProdutoRepository produtoRepository = new ProdutoRepository();

    public void salvarPedido(Pedido pedido) {
        if (pedido == null) {
            return;
        }

        String sqlPedido = "INSERT INTO pedido (cliente_cpf, caixa_id, data_hora, status, observacoes, forma_pagamento, preco_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido)) {

            stmtPedido.setString(1, pedido.getCliente() != null ? pedido.getCliente().getCpf() : null);
            stmtPedido.setNull(2, java.sql.Types.BIGINT);
            stmtPedido.setTimestamp(3, Timestamp.valueOf(pedido.getDataHora()));
            stmtPedido.setString(4, pedido.getStatus().name());
            stmtPedido.setString(5, pedido.getObservacoes());
            stmtPedido.setString(6, pedido.getFormaPagamento() != null ? pedido.getFormaPagamento().name() : null);
            stmtPedido.setBigDecimal(7, pedido.getPrecoTotal());

            try (ResultSet rsPedido = stmtPedido.executeQuery()) {
                if (rsPedido.next()) {
                    pedido.setId(rsPedido.getLong(1));
                }
            }

            String sqlDeleteItens = "DELETE FROM item_pedido WHERE pedido_id = ?";
            try (PreparedStatement stmtDeleteItens = conn.prepareStatement(sqlDeleteItens)) {
                stmtDeleteItens.setLong(1, pedido.getId());
                stmtDeleteItens.executeUpdate();
            }

            if (pedido.getItens() != null) {
                String sqlItem = "INSERT INTO item_pedido (pedido_id, produto_id, nome_produto, preco_unitario, quantidade, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
                for (ItemPedido item : pedido.getItens()) {
                    if (item == null || item.getProduto() == null) {
                        continue;
                    }

                    try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                        stmtItem.setLong(1, pedido.getId());
                        stmtItem.setLong(2, item.getProduto().getId());
                        stmtItem.setString(3, item.getNomeProduto());
                        stmtItem.setBigDecimal(4, item.getPrecoUnitario());
                        stmtItem.setInt(5, item.getQuantidade());
                        stmtItem.setBigDecimal(6, item.getSubtotal());
                        stmtItem.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar pedido: " + e.getMessage());
        }
    }

    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido ORDER BY data_hora DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }

        return pedidos;
    }

    public Pedido buscarPorId(Long id) {
        String sql = "SELECT * FROM pedido WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pedido por id: " + e.getMessage());
        }

        return null;
    }

    public List<Pedido> buscarPedidosPorCpfDeCliente(String cpf) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido WHERE cliente_cpf = ? ORDER BY data_hora DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pedidos por CPF: " + e.getMessage());
        }

        return pedidos;
    }

    public void removerPedido(Long id) {
        String sqlItens = "DELETE FROM item_pedido WHERE pedido_id = ?";
        String sqlPedido = "DELETE FROM pedido WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            try (PreparedStatement stmtItens = conn.prepareStatement(sqlItens)) {
                stmtItens.setLong(1, id);
                stmtItens.executeUpdate();
            }

            try (PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido)) {
                stmtPedido.setLong(1, id);
                stmtPedido.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao remover pedido: " + e.getMessage());
        }
    }

    /**
     * Atualiza apenas o status do pedido e, opcionalmente, o caixa ao qual
     * ele fica associado (usado ao confirmar pagamento ou cancelar).
     * Não mexe nos itens nem em outros dados do pedido.
     */
    public void atualizarStatusECaixa(Long pedidoId, StatusPedido status, Long caixaId) {
        String sql = "UPDATE pedido SET status = ?, caixa_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            if (caixaId != null) {
                stmt.setLong(2, caixaId);
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }
            stmt.setLong(3, pedidoId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do pedido.", e);
        }
    }

    public List<Pedido> buscarPedidosPorCaixa(Long caixaId) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido WHERE caixa_id = ? ORDER BY data_hora DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, caixaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos do caixa.", e);
        }

        return pedidos;
    }

    public BigDecimal calcularSaldoDevedor(String cpf) {
        BigDecimal saldo = BigDecimal.ZERO;
        for (Pedido pedido : buscarPedidosPorCpfDeCliente(cpf)) {
            if (pedido.getFormaPagamento() == FormaPagamento.FIADO
                    && pedido.getStatus() != StatusPedido.FINALIZADO
                    && pedido.getStatus() != StatusPedido.CANCELADO) {
                saldo = saldo.add(pedido.getPrecoTotal());
            }
        }
        return saldo;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
        pedido.setObservacoes(rs.getString("observacoes"));
        pedido.setPrecoTotal(rs.getBigDecimal("preco_total"));

        String clienteCpf = rs.getString("cliente_cpf");
        if (clienteCpf != null && !clienteCpf.isBlank()) {
            pedido.setCliente(clienteRepository.buscarPorCpf(clienteCpf).orElse(null));
        }

        String formaPagamento = rs.getString("forma_pagamento");
        if (formaPagamento != null && !formaPagamento.isBlank()) {
            pedido.setFormaPagamento(FormaPagamento.valueOf(formaPagamento));
        }

        pedido.setItens(buscarItensDoPedido(pedido.getId()));
        return pedido;
    }

    private List<ItemPedido> buscarItensDoPedido(Long pedidoId) {
        List<ItemPedido> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_pedido WHERE pedido_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemPedido item = new ItemPedido();
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setSubtotal(rs.getBigDecimal("subtotal"));
                    item.setNomeProduto(rs.getString("nome_produto"));
                    item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));

                    long produtoId = rs.getLong("produto_id");
                    if (!rs.wasNull()) {
                        item.setProduto(produtoRepository.buscarProduto(produtoId));
                    }
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar itens do pedido: " + e.getMessage());
        }

        return itens;
    }
}