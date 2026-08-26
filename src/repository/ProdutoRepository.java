package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import infrastructure.database.ConnectionFactory;
import model.Produto;
import model.enums.CategoriaProduto;

public class ProdutoRepository {

    public boolean salvarProduto(Produto produto) {
        if (produto == null) {
            return false;
        }

        String sql = "INSERT INTO produto (nome, categoria, preco, qtd_estoque) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria().name());
            stmt.setBigDecimal(3, produto.getPreco());
            stmt.setInt(4, produto.getQtdEstoque());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produto.setId(rs.getLong(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar produto: " + e.getMessage());
        }

        return false;
    }

    public List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapearProduto(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }

        return produtos;
    }

    public Produto buscarProduto(Long id) {
        String sql = "SELECT * FROM produto WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProduto(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        }

        return null;
    }

    public Produto buscarProduto(String nome) {
        String sql = "SELECT * FROM produto WHERE LOWER(nome) = LOWER(?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProduto(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        }

        return null;
    }

    public boolean excluirProduto(Long id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir produto: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarProduto(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, categoria = ?, preco = ?, qtd_estoque = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria().name());
            stmt.setBigDecimal(3, produto.getPreco());
            stmt.setInt(4, produto.getQtdEstoque());
            stmt.setLong(5, produto.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
            return false;
        }
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id"));
        produto.setNome(rs.getString("nome"));
        produto.setCategoria(CategoriaProduto.valueOf(rs.getString("categoria")));
        produto.setPreco(rs.getBigDecimal("preco"));
        produto.setQtdEstoque(rs.getInt("qtd_estoque"));
        return produto;
    }
}
