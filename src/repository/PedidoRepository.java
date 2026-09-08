package repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import infrastructure.database.ConnectionFactory;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.enums.FormaPagamento;
import model.enums.StatusPedido;

public class PedidoRepository {
    private final ClienteRepository clienteRepository = new ClienteRepository();

    public void salvarPedido(Pedido pedido) {
        salvarPedido(pedido, null);
    }

    /**
     * Salva o pedido já associado ao caixa que estava aberto no momento do
     * registro.
     */
    public void salvarPedido(Pedido pedido, Long caixaId) {
        if (pedido == null) {
            return;
        }

        String sqlPedido = "INSERT INTO pedido (cliente_cpf, caixa_id, data_hora, status, observacoes, forma_pagamento, preco_total, valor_pago) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido)) {

            stmtPedido.setString(1, pedido.getCliente() != null ? pedido.getCliente().getCpf() : null);
            if (caixaId != null) {
                stmtPedido.setLong(2, caixaId);
            } else {
                stmtPedido.setNull(2, java.sql.Types.BIGINT);
            }
            stmtPedido.setTimestamp(3, Timestamp.valueOf(pedido.getDataHora()));
            stmtPedido.setString(4, pedido.getStatus().name());
            stmtPedido.setString(5, pedido.getObservacoes());
            stmtPedido.setString(6, pedido.getFormaPagamento() != null ? pedido.getFormaPagamento().name() : null);
            stmtPedido.setBigDecimal(7, pedido.getPrecoTotal());
            stmtPedido.setBigDecimal(8, pedido.getValorPago());

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
        Map<Long, Pedido> pedidosPorId = new LinkedHashMap<>();
        String sql = "SELECT * FROM pedido ORDER BY data_hora DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = mapearPedidoSemItens(rs); // novo método, igual ao mapearPedido mas SEM chamar
                                                          // buscarItensDoPedido
                pedido.setItens(new ArrayList<>());
                pedidosPorId.put(pedido.getId(), pedido);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
            return pedidos;
        }

        if (pedidosPorId.isEmpty()) {
            return pedidos;
        }

        // Busca TODOS os itens de TODOS os pedidos listados em uma única query
        String sqlItens = """
                SELECT ip.pedido_id,
                       ip.quantidade,
                       ip.subtotal,
                       p.id AS produto_id,
                       p.nome,
                       p.categoria,
                       p.preco,
                       p.qtd_estoque
                  FROM item_pedido ip
                  JOIN produto p ON p.id = ip.produto_id
                 WHERE ip.pedido_id = ANY(?)
                """;

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlItens)) {

            Long[] ids = pedidosPorId.keySet().toArray(new Long[0]);
            stmt.setArray(1, conn.createArrayOf("bigint", ids));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto();
                    produto.setId(rs.getLong("produto_id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setCategoria(model.enums.CategoriaProduto.valueOf(rs.getString("categoria")));
                    produto.setPreco(rs.getBigDecimal("preco"));
                    produto.setQtdEstoque(rs.getInt("qtd_estoque"));

                    ItemPedido item = new ItemPedido();
                    item.setProduto(produto);
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setSubtotal(rs.getBigDecimal("subtotal"));

                    Long pedidoId = rs.getLong("pedido_id");
                    pedidosPorId.get(pedidoId).adicionarItem(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar itens dos pedidos: " + e.getMessage());
        }

        pedidos.addAll(pedidosPorId.values());
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

    public void atualizarValorPago(Long pedidoId, BigDecimal valorPago) {
        String sql = "UPDATE pedido SET valor_pago = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, valorPago);
            stmt.setLong(2, pedidoId);
            if (stmt.executeUpdate() == 0) {
                throw new RuntimeException("Pedido não encontrado para atualizar o valor pago.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar valor pago do pedido.", e);
        }
    }

    public List<Pedido> buscarPedidosFiadoEmAbertoPorCliente(String cpf) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido WHERE cliente_cpf = ? AND forma_pagamento = 'FIADO' "
                + "AND status NOT IN ('FINALIZADO', 'CANCELADO') ORDER BY data_hora ASC, id ASC";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            return pedidos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fiados em aberto do cliente.", e);
        }
    }

    public Long buscarCaixaIdDoPedido(Long pedidoId) {
        String sql = "SELECT caixa_id FROM pedido WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long caixaId = rs.getLong(1);
                    return rs.wasNull() ? null : caixaId;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o caixa do pedido.", e);
        }
        return null;
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
        String sql = """
                SELECT COALESCE(SUM(preco_total), 0) AS saldo
                  FROM pedido
                 WHERE cliente_cpf = ?
                   AND forma_pagamento = 'FIADO'
                   AND status NOT IN ('FINALIZADO', 'CANCELADO')
                """;

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("saldo").setScale(2, java.math.RoundingMode.HALF_UP);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao calcular saldo devedor: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
        pedido.setObservacoes(rs.getString("observacoes"));
        pedido.setPrecoTotal(rs.getBigDecimal("preco_total"));
        pedido.setValorPago(rs.getBigDecimal("valor_pago"));

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

    private Pedido mapearPedidoSemItens(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
        pedido.setObservacoes(rs.getString("observacoes"));
        pedido.setPrecoTotal(rs.getBigDecimal("preco_total"));
        pedido.setValorPago(rs.getBigDecimal("valor_pago"));

        String clienteCpf = rs.getString("cliente_cpf");
        if (clienteCpf != null && !clienteCpf.isBlank()) {
            pedido.setCliente(clienteRepository.buscarPorCpf(clienteCpf).orElse(null));
        }

        String formaPagamento = rs.getString("forma_pagamento");
        if (formaPagamento != null && !formaPagamento.isBlank()) {
            pedido.setFormaPagamento(FormaPagamento.valueOf(formaPagamento));
        }

        return pedido;
    }

    private List<ItemPedido> buscarItensDoPedido(Long pedidoId) {
        List<ItemPedido> itens = new ArrayList<>();
        String sql = """
                SELECT ip.quantidade,
                       ip.subtotal,
                       p.id AS produto_id,
                       p.nome,
                       p.categoria,
                       p.preco,
                       p.qtd_estoque
                  FROM item_pedido ip
                  JOIN produto p ON p.id = ip.produto_id
                 WHERE ip.pedido_id = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto();
                    produto.setId(rs.getLong("produto_id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setCategoria(model.enums.CategoriaProduto.valueOf(rs.getString("categoria")));
                    produto.setPreco(rs.getBigDecimal("preco"));
                    produto.setQtdEstoque(rs.getInt("qtd_estoque"));

                    ItemPedido item = new ItemPedido();
                    item.setProduto(produto);
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setSubtotal(rs.getBigDecimal("subtotal"));

                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar itens do pedido: " + e.getMessage());
        }

        return itens;
    }
}