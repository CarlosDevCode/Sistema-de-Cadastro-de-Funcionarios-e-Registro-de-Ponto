/**
 * Lancada quando se tenta cadastrar um funcionario com um CPF ja existente.
 */
public class FuncionarioDuplicadoException extends SistemaException {

    public FuncionarioDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
