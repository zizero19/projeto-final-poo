package repository;

import java.util.ArrayList;
import model.Cliente;

public class ClienteRepository {

    private ArrayList<Cliente> clientes = new ArrayList<>();

    // CREATE
    public void adicionar(Cliente cliente) {
        clientes.add(cliente);
    }

    // READ
    public ArrayList<Cliente> listar() {
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

    public Cliente buscarPorMatricula(String matricula) {
        for (Cliente cliente : clientes) {
            if (cliente.getMatricula().equals(matricula)) {
                return cliente;
            }
        }
        return null;
    }

    // UPDATE
    public boolean atualizar(String cpf, Cliente novoCliente) {
        Cliente cliente = buscarPorCpf(cpf);

        if (cliente != null) {
            cliente.setNome(novoCliente.getNome());
            cliente.setCpf(novoCliente.getCpf());
            cliente.setEmail(novoCliente.getEmail());
            cliente.setMatricula(novoCliente.getMatricula());
            cliente.setTelefone(novoCliente.getTelefone());
            return true;
        }

        return false;
    }

    // DELETE
    public boolean remover(String cpf) {
        Cliente cliente = buscarPorCpf(cpf);

        if (cliente != null) {
            clientes.remove(cliente);
            return true;
        }

        return false;
    }
}