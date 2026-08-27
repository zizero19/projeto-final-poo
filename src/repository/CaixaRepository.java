package repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import infrastructure.database.ConnectionFactory;
import model.Caixa;

public class CaixaRepository {

    public boolean salvarCaixa(Caixa caixa) {
        if (caixa == null) {
            return false;
        }

        String sql = "INSERT INTO caixa (total_vendas, is_aberto, abertura, fechamento) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, caixa.getTotalVendas());
            stmt.setBoolean(2, caixa.isAberto());
            stmt.setTimestamp(3, Timestamp.valueOf(caixa.getAbertura()));
            stmt.setTimestamp(4, caixa.getFechamento() != null ? Timestamp.valueOf(caixa.getFechamento()) : null);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    caixa.setId(rs.getLong(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar caixa: " + e.getMessage());
            return false;
        }

        return false;
    }

    public List<Caixa> listarCaixas() {
        List<Caixa> caixas = new ArrayList<>();
        String sql = "SELECT * FROM caixa ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                caixas.add(mapearCaixa(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar caixas: " + e.getMessage());
        }

        return caixas;
    }

    public Caixa buscarPorId(Long id) {
        String sql = "SELECT * FROM caixa WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCaixa(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar caixa por id: " + e.getMessage());
        }

        return null;
    }

    public Caixa buscarPorData(LocalDate data) {
        String sql = "SELECT * FROM caixa WHERE DATE(abertura) = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(data));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCaixa(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar caixa por data: " + e.getMessage());
        }

        return null;
    }

    /**
     * Fecha o caixa no banco de dados (persiste o que antes só acontecia
     * em memória via Caixa.fechar()).
     */
    public void fecharCaixa(Long id, LocalDateTime fechamento) {
        String sql = "UPDATE caixa SET is_aberto = false, fechamento = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(fechamento));
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fechar caixa.", e);
        }
    }

    /**
     * Soma o valor de uma venda finalizada ao total já registrado no caixa.
     */
    public void incrementarTotalVendas(Long id, BigDecimal valor) {
        String sql = "UPDATE caixa SET total_vendas = total_vendas + ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, valor);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar total de vendas do caixa.", e);
        }
    }

    public Caixa buscarCaixaAberto() {
        String sql = "SELECT * FROM caixa WHERE is_aberto = true ORDER BY abertura DESC LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapearCaixa(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar caixa aberto: " + e.getMessage());
        }

        return null;
    }

    private Caixa mapearCaixa(ResultSet rs) throws SQLException {
        Caixa caixa = new Caixa();
        caixa.setId(rs.getLong("id"));
        caixa.setTotalVendas(rs.getBigDecimal("total_vendas"));
        caixa.setAberto(rs.getBoolean("is_aberto"));
        if (rs.getTimestamp("abertura") != null) {
            caixa.setAbertura(rs.getTimestamp("abertura").toLocalDateTime());
        }
        if (rs.getTimestamp("fechamento") != null) {
            caixa.setFechamento(rs.getTimestamp("fechamento").toLocalDateTime());
        }
        return caixa;
    }
}
