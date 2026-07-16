package repository;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Turma;

public class TurmaRepository {
    private List<Turma> turmas;

    public TurmaRepository() {
        this.turmas = new ArrayList<>();
    }

    public void salvarTurma(Turma turma) {
        if (turma == null) {
            JOptionPane.showMessageDialog(null, "Turma não pode ser nula.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (buscarTurma(turma.getId()) != null) {
            JOptionPane.showMessageDialog(null, "Turma com ID " + turma.getId() + " já existe.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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

    public void excluirTurma(int id) {
        Turma turma = buscarTurma(id);
        if (turma != null) {
            turmas.remove(turma);
        } else {
            JOptionPane.showMessageDialog(null, "Turma não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
