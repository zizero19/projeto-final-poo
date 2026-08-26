package model;

import java.util.ArrayList;
import java.util.List;

import model.enums.DiaSemana;
import model.enums.Turno;

public class Turma {
    private Long id;
    private String nomeTurma;
    private int qtdALunos;
    private Turno turno;
    private boolean isAtivo;
    private List<DiaSemana> diasAula;

    public Turma() {
    }

    public Turma(String nomeTurma, int qtdALunos, Turno turno, boolean isAtivo, List<DiaSemana> diasAula) {
        this.nomeTurma = nomeTurma;
        this.qtdALunos = qtdALunos;
        this.turno = turno;
        this.isAtivo = isAtivo;
        this.diasAula = diasAula != null ? new ArrayList<>(diasAula) : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeTurma() {
        return nomeTurma;
    }

    public void setNomeTurma(String nomeTurma) {
        this.nomeTurma = nomeTurma;
    }

    public int getQtdALunos() {
        return qtdALunos;
    }

    public void setQtdALunos(int qtdALunos) {
        this.qtdALunos = qtdALunos;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean isAtivo) {
        this.isAtivo = isAtivo;
    }

    public List<DiaSemana> getDiasAula() {
        return diasAula;
    }

    public void setDiasAula(List<DiaSemana> diasAula) {
        this.diasAula = diasAula != null ? new ArrayList<>(diasAula) : new ArrayList<>();
    }
}
