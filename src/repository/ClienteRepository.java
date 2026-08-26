package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import infrastructure.database.ConnectionFactory;
import model.Cliente;
import model.Turma;

public class ClienteRepository {

    public Cliente salvarCliente(Cliente cliente) {
        String sql = """
                INSERT INTO cliente
                    (nome, cpf, email, telefone, turma_id, is_devendo)
                VALUES
                    (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());

            if (cliente.getTurmaMatriculada() != null
                    && cliente.getTurmaMatriculada().getId() != null) {
                stmt.setLong(5, cliente.getTurmaMatriculada().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.setBoolean(6, cliente.isDevendo());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong("id"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente.", e);
        }

        throw new RuntimeException("Não foi possível salvar o cliente.");
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            return clientes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes.", e);
        }
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por CPF.", e);
        }
    }

    public List<Cliente> buscarClientesPorNome(String texto) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = """
                SELECT * FROM cliente
                 WHERE LOWER(nome) LIKE LOWER(?)
                 ORDER BY
                     CASE WHEN LOWER(nome) LIKE LOWER(?) THEN 0 ELSE 1 END,
                     nome
                """;

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + texto + "%");
            stmt.setString(2, texto + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
            return clientes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar clientes por nome.", e);
        }
    }

    public Cliente atualizarCliente(Cliente cliente) {
        String sql = """
                UPDATE cliente
                   SET nome = ?,
                       cpf = ?,
                       email = ?,
                       telefone = ?,
                       turma_id = ?,
                       is_devendo = ?
                 WHERE id = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());

            if (cliente.getTurmaMatriculada() != null
                    && cliente.getTurmaMatriculada().getId() != null) {
                stmt.setLong(5, cliente.getTurmaMatriculada().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.setBoolean(6, cliente.isDevendo());
            stmt.setLong(7, cliente.getId());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new RuntimeException("Cliente não encontrado para atualização.");
            }

            return cliente;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente.", e);
        }
    }

    public void excluirCliente(String cpf) {
        String sql = "DELETE FROM cliente WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir cliente.", e);
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();

        cliente.setId(rs.getLong("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setDevendo(rs.getBoolean("is_devendo"));

        long turmaId = rs.getLong("turma_id");
        if (!rs.wasNull()) {
            Turma turma = new TurmaRepository().buscarTurma(turmaId);
            cliente.setTurmaMatriculada(turma);
        }

        return cliente;
    }
}
