# Sistema de Cadastro de Funcionários e Registro de Ponto

Projeto final desenvolvido para a disciplina de **Programação Orientada a Objetos (Java)**, do curso de Tecnologia em Análise e Desenvolvimento de Sistemas.

## Sobre o projeto

O tema proposto pela disciplina foi um sistema de cadastro de funcionários e registro de ponto, com o objetivo de aplicar na prática os conteúdos estudados ao longo do curso: encapsulamento, herança, polimorfismo, classes abstratas, tratamento de exceções e persistência de dados em arquivo.

O sistema simula uma aplicação de RH bem simples, com dois perfis de acesso:

- **Funcionário** — informa o próprio CPF e registra entrada/saída no ponto, além de consultar suas horas trabalhadas e salário estimado.
- **Administrador** — acesso protegido por senha, permite cadastrar, editar, excluir e desligar funcionários, além de gerar relatórios individuais e gerais.

Todos os dados (funcionários e registros de ponto) são salvos automaticamente em arquivos de texto (`.txt`), garantindo que nada se perca entre uma execução e outra do programa.

## Objetivos da disciplina aplicados neste trabalho

A proposta do trabalho era construir um sistema completo aplicando, de forma coerente, os principais pilares da orientação a objetos vistos em sala. Este projeto foi estruturado para colocar cada um deles em uso real, e não apenas como exercício isolado:

- Modelar o domínio do problema (funcionários) usando **classe abstrata** e **herança**, evitando duplicação de código entre os diferentes tipos de funcionário.
- Usar **polimorfismo** para que cada tipo de funcionário calcule o próprio salário com sua própria regra de negócio.
- Tratar situações de erro (dados inválidos, cadastro duplicado, busca sem resultado) com **exceções personalizadas**, em vez de deixar o programa quebrar ou usar apenas mensagens genéricas.
- Persistir os dados da aplicação em **arquivo**, para que o sistema não dependa apenas da memória durante a execução.

## Funcionalidades

**Menu do funcionário:**
- Registrar entrada
- Registrar saída
- Consultar registros de ponto, total de horas trabalhadas e salário estimado

**Menu do administrador** (senha padrão: `admin123`):
- Cadastrar funcionário (CLT ou Gerente)
- Editar nome, cargo e telefone de um funcionário
- Excluir funcionário
- Desligar funcionário (sem excluir o histórico)
- Gerar relatório individual
- Gerar relatório geral (todos os funcionários e total da folha de pagamento)
- Listar todos os funcionários cadastrados

## Estrutura de classes

- **`Funcionario`** (abstrata) — atributos e comportamentos comuns a qualquer funcionário: nome, cpf, telefone, cargo, data de admissão, status (ativo/desligado) e a lista de registros de ponto. Declara os métodos abstratos `calcularSalario()` e `getTipo()`, que cada subclasse implementa à sua maneira.
- **`FuncionarioCLT`** (herda de `Funcionario`) — tem salário base fixo e paga hora extra sobre as horas que ultrapassarem 220h mensais, calculadas a partir dos próprios registros de ponto.
- **`Gerente`** (herda de `Funcionario`) — recebe salário fixo acrescido de um bônus; também pode bater ponto (para fins de controle de frequência), mas isso não altera seu salário.
- **`RegistroPonto`** — representa um dia de trabalho (data, hora de entrada, hora de saída) e calcula as horas trabalhadas naquele dia.
- **`GerenciadorFuncionarios`** — classe de serviço responsável pelo cadastro (CRUD), geração de relatórios e por toda a persistência em arquivo `.txt`.
- **`SistemaException`** (exceção personalizada base, checked) e suas subclasses `FuncionarioDuplicadoException`, `FuncionarioNaoEncontradoException` e `DadosInvalidosException`.
- **`Main`** — camada de apresentação (console): menu do funcionário e menu do administrador.

## Conceitos de POO aplicados

| Conceito | Onde aparece no código |
|---|---|
| Encapsulamento | Atributos `protected`/`private` com getters e setters em `Funcionario` e subclasses |
| Classe abstrata | `Funcionario`, com os métodos abstratos `calcularSalario()` e `getTipo()` |
| Herança | `FuncionarioCLT` e `Gerente` estendem `Funcionario` |
| Polimorfismo | Cada subclasse calcula o salário de forma diferente; `toString()` e `toFileString()` também mudam de comportamento conforme o tipo real do objeto |
| Sobrecarga de construtores | `FuncionarioCLT`, `Gerente` e `RegistroPonto` possuem mais de um construtor, para cadastro novo e para carregamento a partir do arquivo |
| Exceções personalizadas | `SistemaException` como classe base, especializada em `FuncionarioDuplicadoException`, `FuncionarioNaoEncontradoException` e `DadosInvalidosException` |
| Tratamento de exceções | Uso de `try-catch`, `throw` e `throws` em todo o fluxo do menu administrativo e de registro de ponto |
| Persistência em arquivo | `GerenciadorFuncionarios` salva e carrega os dados de `funcionarios.txt` e `pontos.txt`, tratando `IOException` sem derrubar a aplicação |

## Como compilar e executar

Todo o código-fonte está na pasta `src/`, sem uso de pacotes (`package`):

```bash
cd src
javac *.java
java Main
```

O programa cria automaticamente os arquivos `funcionarios.txt` e `pontos.txt` na pasta onde for executado — é neles que os dados ficam salvos entre uma execução e outra.

**Senha do administrador:** `admin123` (pode ser alterada na constante `SENHA_ADMIN`, no início da classe `Main`).

## Possíveis melhorias futuras

- Validar o formato do CPF (11 dígitos, dígitos verificadores)
- Criptografar a senha do administrador em vez de mantê-la fixa no código-fonte
- Registrar histórico de alterações salariais
- Migrar a persistência de `.txt` para um banco de dados relacional (JDBC) ou formato estruturado como JSON