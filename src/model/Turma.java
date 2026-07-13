package model;

import model.enums.Turno;

public class Turma {
    private int id;
    private String nomeTurma;
    private int qtdALunos;
    private Turno turno;
    private boolean isAtivo;

    public Turma() {
    }

    public Turma(int id, String nomeTurma, int qtdALunos, Turno turno, boolean isAtivo) {
        this.id = id;
        this.nomeTurma = nomeTurma;
        this.qtdALunos = qtdALunos;
        this.turno = turno;
        this.isAtivo = isAtivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
