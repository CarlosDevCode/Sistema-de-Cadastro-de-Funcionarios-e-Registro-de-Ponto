import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Classe principal do Sistema de Cadastro de Funcionarios e Registro de Ponto.
 * Contem o menu de acesso do funcionario (registro de ponto, disponivel para
 * qualquer tipo de funcionario) e o menu de acesso do administrador
 * (protegido por senha), que permite cadastrar, editar, excluir/desligar
 * funcionarios e gerar relatorios.
 */
public class Main {

    private static final String SENHA_ADMIN = "admin123";
    private static final GerenciadorFuncionarios gerenciador = new GerenciadorFuncionarios();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n===== SISTEMA DE CADASTRO DE FUNCIONARIOS E PONTO =====");
            System.out.println("1 - Acesso Funcionario");
            System.out.println("2 - Acesso Administrador");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opcao: ");
            opcao = lerInteiro();

            switch (opcao) {
                case 1 -> menuFuncionario();
                case 2 -> menuAdministrador();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    // ---------------- Menu do Funcionario ----------------

    private static void menuFuncionario() {
        System.out.print("Informe seu CPF: ");
        String cpf = sc.nextLine();
        Funcionario f;
        try {
            f = gerenciador.buscarPorCpf(cpf);
        } catch (FuncionarioNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }

        if (!f.isAtivo()) {
            System.out.println("Este funcionario esta desligado e nao pode registrar ponto.");
            return;
        }

        int opcao;
        do {
            System.out.println("\n--- Menu do Funcionario: " + f.getNome() + " (" + f.getTipo() + ") ---");
            System.out.println("1 - Registrar entrada");
            System.out.println("2 - Registrar saida");
            System.out.println("3 - Ver meus registros e horas trabalhadas");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");
            opcao = lerInteiro();

            try {
                switch (opcao) {
                    case 1 -> {
                        LocalTime hora = LocalTime.now().withSecond(0).withNano(0);
                        f.registrarEntrada(LocalDate.now(), hora);
                        gerenciador.salvarDados();
                        System.out.println("Entrada registrada as " + hora);
                    }
                    case 2 -> {
                        LocalTime hora = LocalTime.now().withSecond(0).withNano(0);
                        f.registrarSaida(LocalDate.now(), hora);
                        gerenciador.salvarDados();
                        System.out.println("Saida registrada as " + hora);
                    }
                    case 3 -> {
                        if (f.getRegistros().isEmpty()) {
                            System.out.println("Nenhum registro encontrado.");
                        } else {
                            for (RegistroPonto r : f.getRegistros()) {
                                System.out.println(r);
                            }
                        }
                        System.out.printf("Total de horas trabalhadas: %.2f%n", f.getTotalHorasTrabalhadas());
                        System.out.printf("Salario estimado: R$ %.2f%n", f.calcularSalario());
                    }
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (DadosInvalidosException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    // ---------------- Menu do Administrador ----------------

    private static void menuAdministrador() {
        System.out.print("Digite a senha de administrador: ");
        String senha = sc.nextLine();
        if (!senha.equals(SENHA_ADMIN)) {
            System.out.println("Senha incorreta. Acesso negado.");
            return;
        }

        int opcao;
        do {
            System.out.println("\n--- Menu do Administrador ---");
            System.out.println("1 - Cadastrar funcionario");
            System.out.println("2 - Editar funcionario (nome/cargo/telefone)");
            System.out.println("3 - Excluir funcionario");
            System.out.println("4 - Desligar funcionario");
            System.out.println("5 - Relatorio individual");
            System.out.println("6 - Relatorio geral");
            System.out.println("7 - Listar todos os funcionarios");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");
            opcao = lerInteiro();

            try {
                switch (opcao) {
                    case 1 -> cadastrarFuncionario();
                    case 2 -> editarFuncionario();
                    case 3 -> excluirFuncionario();
                    case 4 -> desligarFuncionario();
                    case 5 -> System.out.println(gerenciador.relatorioIndividual(lerCpf()));
                    case 6 -> System.out.println(gerenciador.relatorioGeral());
                    case 7 -> listarTodos();
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (SistemaException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Erro: valor numerico invalido.");
            } catch (DateTimeParseException e) {
                System.out.println("Erro: data invalida. Use o formato AAAA-MM-DD.");
            }
        } while (opcao != 0);
    }

    private static String lerCpf() {
        System.out.print("Informe o CPF: ");
        return sc.nextLine();
    }

    private static void cadastrarFuncionario() throws SistemaException {
        System.out.println("Tipo de funcionario: 1 - CLT | 2 - Gerente");
        int tipo = lerInteiro();

        System.out.print("Nome: ");
        String nome = sc.nextLine();
        if (nome.isBlank()) {
            throw new DadosInvalidosException("O nome nao pode ser vazio.");
        }

        System.out.print("CPF: ");
        String cpf = sc.nextLine();
        if (cpf.isBlank()) {
            throw new DadosInvalidosException("O CPF nao pode ser vazio.");
        }

        System.out.print("Telefone de contato: ");
        String telefone = sc.nextLine();
        if (telefone.isBlank()) {
            throw new DadosInvalidosException("O telefone nao pode ser vazio.");
        }

        System.out.print("Cargo: ");
        String cargo = sc.nextLine();

        System.out.print("Data de admissao (AAAA-MM-DD): ");
        LocalDate dataAdmissao = LocalDate.parse(sc.nextLine());

        Funcionario novo;
        if (tipo == 1) {
            System.out.print("Salario base: ");
            double salarioBase = lerDouble();
            System.out.print("Valor da hora extra: ");
            double valorHoraExtra = lerDouble();
            novo = new FuncionarioCLT(nome, cpf, telefone, cargo, dataAdmissao, salarioBase, valorHoraExtra);
        } else if (tipo == 2) {
            System.out.print("Salario fixo: ");
            double salarioFixo = lerDouble();
            System.out.print("Bonus: ");
            double bonus = lerDouble();
            novo = new Gerente(nome, cpf, telefone, cargo, dataAdmissao, salarioFixo, bonus);
        } else {
            throw new DadosInvalidosException("Tipo de funcionario invalido.");
        }

        gerenciador.cadastrar(novo);
        System.out.println("Funcionario cadastrado com sucesso! ID gerado: " + novo.getId());
    }

    private static void editarFuncionario() throws FuncionarioNaoEncontradoException {
        String cpf = lerCpf();
        System.out.print("Novo nome (deixe vazio para nao alterar): ");
        String nome = sc.nextLine();
        System.out.print("Novo cargo (deixe vazio para nao alterar): ");
        String cargo = sc.nextLine();
        System.out.print("Novo telefone (deixe vazio para nao alterar): ");
        String telefone = sc.nextLine();
        gerenciador.editarNomeCargoTelefone(cpf, nome, cargo, telefone);
        System.out.println("Funcionario atualizado com sucesso!");
    }

    private static void excluirFuncionario() throws FuncionarioNaoEncontradoException {
        String cpf = lerCpf();
        gerenciador.excluir(cpf);
        System.out.println("Funcionario excluido com sucesso!");
    }

    private static void desligarFuncionario() throws FuncionarioNaoEncontradoException {
        String cpf = lerCpf();
        gerenciador.desligar(cpf);
        System.out.println("Funcionario desligado com sucesso!");
    }

    private static void listarTodos() {
        if (gerenciador.listarTodos().isEmpty()) {
            System.out.println("Nenhum funcionario cadastrado.");
            return;
        }
        for (Funcionario f : gerenciador.listarTodos()) {
            System.out.println(f);
        }
    }

    // ---------------- Utilitarios de leitura ----------------

    private static int lerInteiro() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Digite um numero: ");
            }
        }
    }

    private static double lerDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Digite um numero valido: ");
            }
        }
    }
}
