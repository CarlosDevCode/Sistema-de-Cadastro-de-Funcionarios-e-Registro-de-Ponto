//Lancada quando se busca, edita, exclui ou desliga um funcionario
//cujo CPF nao existe na base de dados.

public class FuncionarioNaoEncontradoException extends SistemaException {

    public FuncionarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
