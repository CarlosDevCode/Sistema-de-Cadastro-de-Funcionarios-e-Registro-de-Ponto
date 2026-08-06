import java.time.LocalDate;

//Funcionario do tipo CLT: possui salario base fixo e registra ponto
//diariamente (via metodos herdados de Funcionario). As horas que
//ultrapassarem a carga horaria mensal esperada (220h) sao pagas como
//hora extra, compondo o salario final.

public class FuncionarioCLT extends Funcionario {

    private static final double HORAS_MENSAIS_ESPERADAS = 220.0;

    private double salarioBase;
    private double valorHoraExtra;

    public FuncionarioCLT(String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao,
                           double salarioBase, double valorHoraExtra) {
        super(nome, cpf, telefone, cargo, dataAdmissao);
        this.salarioBase = salarioBase;
        this.valorHoraExtra = valorHoraExtra;
    }

    // Sobrecarga de construtor: caso o administrador nao informe valor de hora extra
    public FuncionarioCLT(String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao,
                           double salarioBase) {
        this(nome, cpf, telefone, cargo, dataAdmissao, salarioBase, 0.0);
    }

    // Construtor usado ao carregar um funcionario CLT ja existente do arquivo (preserva o id)
    protected FuncionarioCLT(int id, String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao,
                              double salarioBase, double valorHoraExtra) {
        super(id, nome, cpf, telefone, cargo, dataAdmissao);
        this.salarioBase = salarioBase;
        this.valorHoraExtra = valorHoraExtra;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public double getValorHoraExtra() {
        return valorHoraExtra;
    }

    public void setValorHoraExtra(double valorHoraExtra) {
        this.valorHoraExtra = valorHoraExtra;
    }

    @Override
    public double calcularSalario() {
        double horas = getTotalHorasTrabalhadas();
        double horasExtras = Math.max(0, horas - HORAS_MENSAIS_ESPERADAS);
        return salarioBase + (horasExtras * valorHoraExtra);
    }

    @Override
    public String getTipo() {
        return "CLT";
    }

    @Override
    public String toFileString() {
        return "CLT;" + id + ";" + nome + ";" + cpf + ";" + telefone + ";" + cargo + ";" + dataAdmissao + ";" + ativo
                + ";" + salarioBase + ";" + valorHoraExtra;
    }
}
