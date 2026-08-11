package model;

import model.enums.Turno;

public class Turma {

    private static int PROXIMO_ID = 1;

    private int id;
    private String nomeTurma;
    private int qtdALunos;
    private Turno turno;
    private boolean isAtivo;
    private String diasAula;

    public Turma() {
        this.id = PROXIMO_ID++;
    }

    public Turma(String nomeTurma, int qtdALunos, Turno turno, boolean isAtivo, String diasAula) {
        this();
        this.nomeTurma = nomeTurma;
        this.qtdALunos = qtdALunos;
        this.turno = turno;
        this.isAtivo = isAtivo;
        this.diasAula = diasAula;
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

    public String getDiasAula() {
        return diasAula;
    }

    public void setDiasAula(String diasAula) {
        this.diasAula = diasAula;
    }

    @Override
    public String toString() {
        return nomeTurma;
    }
}
