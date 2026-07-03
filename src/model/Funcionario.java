package model;

public class Funcionario extends Pessoa {
    private String cargo;
    private String turno;

    public Funcionario() {

    }

    public Funcionario(String nome, String cpf, String email, String cargo, String turno) {
        super(nome, cpf, email);
        this.cargo = cargo;
        this.turno = turno;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

}
