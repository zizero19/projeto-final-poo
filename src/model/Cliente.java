package model;

public class Cliente extends Pessoa {
    private String matricula;
    private String telefone;

    public Cliente() {
    }

    public Cliente(String nome, String cpf, String email, String matricula, String telefone) {
        super(nome, cpf, email);
        this.matricula = matricula;
        this.telefone = telefone;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // TODO: verificar como implementar um método para trazer o histórico de compras
    // do cliente, se necessário.

}
