import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//Classe abstrata que representa um Funcionario da empresa.
//Define os atributos e comportamentos comuns a todos os tipos de funcionario,
//utilizando encapsulamento (atributos protected + getters/setters) e
//deixando o calculo do salario e o tipo como responsabilidade das subclasses
//(polimorfismo).

//O registro de ponto foi colocado aqui na classe base porque tanto o
//funcionario CLT quanto o Gerente podem bater ponto (controle de
//frequencia) — a diferenca entre os tipos fica apenas na forma de
//calcular o salario.

public abstract class Funcionario implements Serializable {

    // Contador estatico responsavel por gerar o proximo id disponivel.
    // Ao carregar dados do arquivo, o contador e ajustado para nao repetir ids.
    private static int proximoId = 1;

    protected final int id;
    protected String nome;
    protected String cpf;
    protected String telefone;
    protected String cargo;
    protected LocalDate dataAdmissao;
    protected boolean ativo;
    protected List<RegistroPonto> registros;

    //Construtor usado ao cadastrar um novo funcionario: o id e gerado
    //automaticamente de forma incremental.
    public Funcionario(String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao) {
        this(proximoId, nome, cpf, telefone, cargo, dataAdmissao);
    }

    //Construtor usado ao carregar um funcionario ja existente a partir do
    //arquivo, preservando o id original.
    protected Funcionario(int id, String nome, String cpf, String telefone, String cargo, LocalDate dataAdmissao) {
        this.id = id;
        if (id >= proximoId) {
            proximoId = id + 1;
        }
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
        this.ativo = true;
        this.registros = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<RegistroPonto> getRegistros() {
        return registros;
    }

    public void registrarEntrada(LocalDate data, LocalTime hora) throws DadosInvalidosException {
        for (RegistroPonto r : registros) {
            if (r.getData().equals(data) && !r.isFechado()) {
                throw new DadosInvalidosException("Ja existe um ponto em aberto na data " + data
                        + ". Registre a saida antes de uma nova entrada.");
            }
        }
        registros.add(new RegistroPonto(data, hora));
    }

    public void registrarSaida(LocalDate data, LocalTime hora) throws DadosInvalidosException {
        for (RegistroPonto r : registros) {
            if (r.getData().equals(data) && !r.isFechado()) {
                if (hora.isBefore(r.getHoraEntrada())) {
                    throw new DadosInvalidosException("A hora de saida nao pode ser anterior a hora de entrada.");
                }
                r.setHoraSaida(hora);
                return;
            }
        }
        throw new DadosInvalidosException("Nao ha ponto de entrada em aberto na data " + data + ".");
    }

    public double getTotalHorasTrabalhadas() {
        double total = 0;
        for (RegistroPonto r : registros) {
            total += r.getHorasTrabalhadas();
        }
        return total;
    }

    //Cada subtipo de funcionario calcula seu salario de forma diferente
    //(polimorfismo em acao).
    public abstract double calcularSalario();

    public abstract String getTipo();

    //Serializa o funcionario em uma linha de texto para persistencia em arquivo.
    public abstract String toFileString();

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Nome: %s | CPF: %s | Telefone: %s | Cargo: %s | Tipo: %s | Admissao: %s | Status: %s | Salario: R$ %.2f",
                id, nome, cpf, telefone, cargo, getTipo(), dataAdmissao, ativo ? "Ativo" : "Desligado", calcularSalario());
    }
}
