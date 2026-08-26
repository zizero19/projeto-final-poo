package service;

import java.util.List;

import model.Cliente;
import repository.ClienteRepository;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente salvarCliente(Cliente cliente) {
        validarCliente(cliente);

        if (clienteRepository.buscarPorCpf(cliente.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente com o CPF informado.");
        }

        return clienteRepository.salvarCliente(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.listarClientes();
    }

    public Cliente buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF deve ser informado.");
        }
        return clienteRepository.buscarPorCpf(cpf).orElse(null);
    }

    public List<Cliente> buscarClientesPorNome(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarClientes();
        }
        return clienteRepository.buscarClientesPorNome(texto.trim());
    }

    public void excluirCliente(String cpf) {
        Cliente cliente = buscarPorCpf(cpf);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        clienteRepository.excluirCliente(cpf);
    }

    public Cliente atualizarCliente(Cliente cliente) {
        validarCliente(cliente);
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("ID do cliente deve ser informado para atualização.");
        }
        return clienteRepository.atualizarCliente(cliente);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do cliente deve ser informado.");
        }
        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF do cliente deve ser informado.");
        }
    }
}
