package repository;

import java.util.ArrayList;
import model.Funcionario;

public class FuncionarioRepository {

    private ArrayList<Funcionario> funcionarios = new ArrayList<>();

    // CREATE
    public void adicionar(Funcionario funcionario) {
        funcionarios.add(funcionario);
    }

    // READ
    public ArrayList<Funcionario> listar() {
        return funcionarios;
    }

    public Funcionario buscarPorCpf(String cpf) {
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getCpf().equals(cpf)) {
                return funcionario;
            }
        }
        return null;
    }

    // UPDATE
    public boolean atualizar(String cpf, Funcionario novoFuncionario) {
        Funcionario funcionario = buscarPorCpf(cpf);

        if (funcionario != null) {
            funcionario.setNome(novoFuncionario.getNome());
            funcionario.setCpf(novoFuncionario.getCpf());
            funcionario.setEmail(novoFuncionario.getEmail());
            funcionario.setCargo(novoFuncionario.getCargo());
            funcionario.setTurno(novoFuncionario.getTurno());
            return true;
        }

        return false;
    }

    // DELETE
    public boolean remover(String cpf) {
        Funcionario funcionario = buscarPorCpf(cpf);

        if (funcionario != null) {
            funcionarios.remove(funcionario);
            return true;
        }

        return false;
    }
}