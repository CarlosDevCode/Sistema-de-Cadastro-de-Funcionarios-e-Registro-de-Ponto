import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Classe responsavel por gerenciar o cadastro de funcionarios (CRUD),
//gerar relatorios e persistir/carregar os dados em arquivos .txt.
public class GerenciadorFuncionarios {

    private static final String ARQUIVO_FUNCIONARIOS = "funcionarios.txt";
    private static final String ARQUIVO_PONTOS = "pontos.txt";

    private List<Funcionario> funcionarios;

    public GerenciadorFuncionarios() {
        this.funcionarios = new ArrayList<>();
        carregarDados();
    }

    public void cadastrar(Funcionario novo) throws FuncionarioDuplicadoException {
        for (Funcionario existente : funcionarios) {
            if (existente.getCpf().equals(novo.getCpf())) {
                throw new FuncionarioDuplicadoException(
                        "Ja existe um funcionario cadastrado com o CPF " + novo.getCpf());
            }
        }
        funcionarios.add(novo);
        salvarDados();
    }

    public Funcionario buscarPorCpf(String cpf) throws FuncionarioNaoEncontradoException {
        for (Funcionario f : funcionarios) {
            if (f.getCpf().equals(cpf)) {
                return f;
            }
        }
        throw new FuncionarioNaoEncontradoException("Funcionario com CPF " + cpf + " nao encontrado.");
    }

    public Funcionario buscarPorId(int id) throws FuncionarioNaoEncontradoException {
        for (Funcionario f : funcionarios) {
            if (f.getId() == id) {
                return f;
            }
        }
        throw new FuncionarioNaoEncontradoException("Funcionario com ID " + id + " nao encontrado.");
    }

    public void excluir(String cpf) throws FuncionarioNaoEncontradoException {
        Funcionario f = buscarPorCpf(cpf);
        funcionarios.remove(f);
        salvarDados();
    }

    public void desligar(String cpf) throws FuncionarioNaoEncontradoException {
        Funcionario f = buscarPorCpf(cpf);
        f.setAtivo(false);
        salvarDados();
    }

    public void editarNomeCargoTelefone(String cpf, String novoNome, String novoCargo, String novoTelefone)
            throws FuncionarioNaoEncontradoException {
        Funcionario f = buscarPorCpf(cpf);
        if (novoNome != null && !novoNome.isBlank()) {
            f.setNome(novoNome);
        }
        if (novoCargo != null && !novoCargo.isBlank()) {
            f.setCargo(novoCargo);
        }
        if (novoTelefone != null && !novoTelefone.isBlank()) {
            f.setTelefone(novoTelefone);
        }
        salvarDados();
    }

    public List<Funcionario> listarTodos() {
        return funcionarios;
    }

    public String relatorioIndividual(String cpf) throws FuncionarioNaoEncontradoException {
        Funcionario f = buscarPorCpf(cpf);
        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATORIO INDIVIDUAL =====\n");
        sb.append(f).append("\n");

        sb.append("--- Registros de ponto ---\n");
        if (f.getRegistros().isEmpty()) {
            sb.append("Nenhum registro de ponto encontrado.\n");
        } else {
            for (RegistroPonto r : f.getRegistros()) {
                sb.append(r).append("\n");
            }
        }
        sb.append(String.format("Total de horas trabalhadas: %.2f\n", f.getTotalHorasTrabalhadas()));
        return sb.toString();
    }

    public String relatorioGeral() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATORIO GERAL DE FUNCIONARIOS =====\n");
        double totalFolha = 0;
        int ativos = 0;

        for (Funcionario f : funcionarios) {
            sb.append(f).append("\n");
            if (f.isAtivo()) {
                totalFolha += f.calcularSalario();
                ativos++;
            }
        }
        sb.append(String.format("Total de funcionarios cadastrados: %d\n", funcionarios.size()));
        sb.append(String.format("Total de funcionarios ativos: %d\n", ativos));
        sb.append(String.format("Total da folha de pagamento (ativos): R$ %.2f\n", totalFolha));
        return sb.toString();
    }

    // ---------------- Persistencia em arquivo ----------------

    //Salva a lista de funcionarios e seus registros de ponto em arquivos .txt.
    //Erros de escrita sao tratados e informados, sem interromper a aplicacao.
    public void salvarDados() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_FUNCIONARIOS))) {
            for (Funcionario f : funcionarios) {
                pw.println(f.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo de funcionarios: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_PONTOS))) {
            for (Funcionario f : funcionarios) {
                for (RegistroPonto r : f.getRegistros()) {
                    pw.println(f.getId() + "|" + r.toFileString());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo de registros de ponto: " + e.getMessage());
        }
    }

    //Carrega os dados persistidos ao iniciar o sistema. Linhas corrompidas
    //ou invalidas sao ignoradas e reportadas, sem interromper a leitura.
    private void carregarDados() {
        File arquivoFuncionarios = new File(ARQUIVO_FUNCIONARIOS);
        if (arquivoFuncionarios.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(arquivoFuncionarios))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    if (linha.isBlank()) {
                        continue;
                    }
                    try {
                        funcionarios.add(parseFuncionario(linha));
                    } catch (Exception e) {
                        System.out.println("Linha invalida ignorada em " + ARQUIVO_FUNCIONARIOS + ": " + linha);
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler " + ARQUIVO_FUNCIONARIOS + ": " + e.getMessage());
            }
        }

        File arquivoPontos = new File(ARQUIVO_PONTOS);
        if (arquivoPontos.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(arquivoPontos))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    if (linha.isBlank()) {
                        continue;
                    }
                    try {
                        int separador = linha.indexOf('|');
                        int id = Integer.parseInt(linha.substring(0, separador));
                        RegistroPonto registro = RegistroPonto.fromFileString(linha.substring(separador + 1));
                        for (Funcionario f : funcionarios) {
                            if (f.getId() == id) {
                                f.getRegistros().add(registro);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Linha invalida ignorada em " + ARQUIVO_PONTOS + ": " + linha);
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler " + ARQUIVO_PONTOS + ": " + e.getMessage());
            }
        }
    }

    private Funcionario parseFuncionario(String linha) {
        String[] p = linha.split(";");
        String tipo = p[0];
        int id = Integer.parseInt(p[1]);
        String nome = p[2];
        String cpf = p[3];
        String telefone = p[4];
        String cargo = p[5];
        LocalDate dataAdmissao = LocalDate.parse(p[6]);
        boolean ativo = Boolean.parseBoolean(p[7]);

        if (tipo.equals("CLT")) {
            double salarioBase = Double.parseDouble(p[8]);
            double valorHoraExtra = Double.parseDouble(p[9]);
            FuncionarioCLT clt = new FuncionarioCLT(id, nome, cpf, telefone, cargo, dataAdmissao, salarioBase, valorHoraExtra);
            clt.setAtivo(ativo);
            return clt;
        } else {
            double salarioFixo = Double.parseDouble(p[8]);
            double bonus = Double.parseDouble(p[9]);
            Gerente g = new Gerente(id, nome, cpf, telefone, cargo, dataAdmissao, salarioFixo, bonus);
            g.setAtivo(ativo);
            return g;
        }
    }
}
