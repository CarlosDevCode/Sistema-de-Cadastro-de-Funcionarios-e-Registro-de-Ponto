import java.time.LocalDate;

//Funcionario do tipo Gerente: recebe salario fixo mensal acrescido de um
//bonus. Assim como o CLT, o gerente tambem pode registrar ponto (metodos
//herdados de Funcionario) para fins de controle de frequencia — porem,
//diferente do CLT, as horas registradas nao alteram o valor do salario,
//que permanece fixo.

public class Gerente extends Funcionario {

    private double salarioFixo;
    private double bonus;

    public Gerente(String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao,
                    double salarioFixo, double bonus) {
        super(nome, cpf, telefone, cargo, dataAdmissao);
        this.salarioFixo = salarioFixo;
        this.bonus = bonus;
    }

    // Sobrecarga de construtor: gerente sem bonus definido
    public Gerente(String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao,
                    double salarioFixo) {
        this(nome, cpf, telefone, cargo, dataAdmissao, salarioFixo, 0.0);
    }

    // Construtor usado ao carregar um gerente ja existente do arquivo (preserva o id)
    protected Gerente(int id, String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao,
                       double salarioFixo, double bonus) {
        super(id, nome, cpf, telefone, cargo, dataAdmissao);
        this.salarioFixo = salarioFixo;
        this.bonus = bonus;
    }

    public double getSalarioFixo() {
        return salarioFixo;
    }

    public void setSalarioFixo(double salarioFixo) {
        this.salarioFixo = salarioFixo;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    @Override
    public double calcularSalario() {
        return salarioFixo + bonus;
    }

    @Override
    public String getTipo() {
        return "GERENTE";
    }

    @Override
    public String toFileString() {
        return "GERENTE;" + id + ";" + nome + ";" + cpf + ";" + telefone + ";" + cargo + ";" + dataAdmissao + ";" + ativo
                + ";" + salarioFixo + ";" + bonus;
    }
}
