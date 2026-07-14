package repository;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Cliente;

public class ClienteRepository {
    private List<Cliente> clientes;

    public ClienteRepository() {
        this.clientes = new ArrayList<>();
    }

    public void salvarCliente(Cliente cliente) {
        if (cliente == null) {
            JOptionPane.showMessageDialog(null, "Cliente não pode ser nulo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (buscarPorCpf(cliente.getCpf()) != null) {
            JOptionPane.showMessageDialog(null, "Cliente com CPF " + cliente.getCpf() + " já existe.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        clientes.add(cliente);
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