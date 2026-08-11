package repository;

import java.util.ArrayList;
import java.util.List;

import model.Turma;

public class TurmaRepository {
    private List<Turma> turmas;

    public TurmaRepository() {
        this.turmas = new ArrayList<>();
    }

    public void salvarTurma(Turma turma) {

        turmas.add(turma);
    }

    public List<Turma> listarTurmas() {
        return turmas;
    }

    public Turma buscarTurma(int id) {
        for (Turma turma : turmas) {
            if (turma.getId() == id) {
                return turma;
            }

        }
        return null;
    }

    public Turma buscarTurma(String nome) {
        for (Turma turma : turmas) {
            if (nome.equalsIgnoreCase(turma.getNomeTurma())) {
                return turma;
            }
        }
        return null;
    }

    public boolean excluirTurma(int id) {
        Turma turma = buscarTurma(id);

        if (turma != null) {
            turmas.remove(turma);
            return true;
        } else {
            return false;
        }
    }

}
