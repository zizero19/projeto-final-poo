package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import infrastructure.database.ConnectionFactory;
import model.Turma;
import model.enums.DiaSemana;
import model.enums.Turno;

public class TurmaRepository {

    public boolean salvarTurma(Turma turma) {
        if (turma == null) {
            return false;
        }

        String sql = "INSERT INTO turma (nome_turma, qtd_alunos, turno, is_ativo, dias_aula) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, turma.getNomeTurma());
            stmt.setInt(2, turma.getQtdALunos());
            stmt.setString(3, turma.getTurno().name());
            stmt.setBoolean(4, turma.isAtivo());
            stmt.setString(5, serializarDiasAula(turma.getDiasAula()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    turma.setId(rs.getLong(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar turma: " + e.getMessage());
        }

        return false;
    }

    public List<Turma> listarTurmas() {
        List<Turma> turmas = new ArrayList<>();
        String sql = "SELECT * FROM turma ORDER BY id";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                turmas.add(mapearTurma(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar turmas: " + e.getMessage());
        }

        return turmas;
    }

    public Turma buscarTurma(Long id) {
        String sql = "SELECT * FROM turma WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTurma(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar turma por id: " + e.getMessage());
        }

        return null;
    }

    public Turma buscarTurma(String nome) {
        String sql = "SELECT * FROM turma WHERE LOWER(nome_turma) = LOWER(?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTurma(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar turma por nome: " + e.getMessage());
        }

        return null;
    }

    public boolean excluirTurma(Long id) {
        String sql = "DELETE FROM turma WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir turma: " + e.getMessage());
            return false;
        }
    }

    private Turma mapearTurma(ResultSet rs) throws SQLException {
        Turma turma = new Turma();
        turma.setId(rs.getLong("id"));
        turma.setNomeTurma(rs.getString("nome_turma"));
        turma.setQtdALunos(rs.getInt("qtd_alunos"));
        turma.setTurno(Turno.valueOf(rs.getString("turno")));
        turma.setAtivo(rs.getBoolean("is_ativo"));
        turma.setDiasAula(deserializarDiasAula(rs.getString("dias_aula")));
        return turma;
    }

    private String serializarDiasAula(List<DiaSemana> diasAula) {
        if (diasAula == null || diasAula.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < diasAula.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(diasAula.get(i).name());
        }
        return builder.toString();
    }

    private List<DiaSemana> deserializarDiasAula(String diasAula) {
        List<DiaSemana> dias = new ArrayList<>();
        if (diasAula == null || diasAula.isBlank()) {
            return dias;
        }

        String[] valores = diasAula.split(",");
        for (String valor : valores) {
            String nome = valor.trim();
            if (!nome.isEmpty()) {
                dias.add(DiaSemana.valueOf(nome));
            }
        }
        return dias;
    }
}
