package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import db.ConnectionFactory;
import model.Cliente;

public class ClienteRepository {

    public boolean salvarCliente(Cliente cliente) {
        if (cliente == null) {
            return false;
        }

        String sql = "INSERT INTO cliente (nome, cpf, email, telefone, turma_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setLong(5, cliente.getTurmaMatriculada().getId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getLong(1));
                }
            }

            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao salvar cliente: " + e.getMessage());
            return false;
        }
    }

    public List<Cliente> listarClientes() {
        return clientes;
    }

    public Cliente buscarPorCpf(String cpf) {
        for (Cliente cliente : clientes) {
            if (cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }
        return null;
    }

    public void excluirCliente(String cpf) {
        Cliente cliente = buscarPorCpf(cpf);

        if (cliente != null) {
            clientes.remove(cliente);
        } else {
            JOptionPane.showMessageDialog(null, "Cliente não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }
}