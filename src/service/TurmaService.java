package service;

import java.util.List;

import model.Turma;
import repository.TurmaRepository;

public class TurmaService {

    private final TurmaRepository turmaRepository;

    public TurmaService(TurmaRepository turmaRepository) {
        this.turmaRepository = turmaRepository;
    }

    public Turma salvarTurma(Turma turma) {
        if (turma == null) {
            throw new IllegalArgumentException("Turma não pode ser nula.");
        }
        return turmaRepository.salvarTurma(turma) ? turma : null;
    }

    public List<Turma> listarTurmas() {
        return turmaRepository.listarTurmas();
    }

    public Turma buscarTurma(Long id) {
        return turmaRepository.buscarTurma(id);
    }

    public Turma buscarTurma(String nome) {
        return turmaRepository.buscarTurma(nome);
    }

    public boolean excluirTurma(Long id) {
        return turmaRepository.excluirTurma(id);
    }

    public boolean atualizarTurma(Turma turma) {
        if (turma == null || turma.getId() == null) {
            throw new IllegalArgumentException("Turma inválida para atualização.");
        }
        return turmaRepository.atualizarTurma(turma);
    }

    public List<Turma> buscarTurmasPorNome(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarTurmas();
        }
        return turmaRepository.buscarTurmasPorNome(texto.trim());
    }
}
