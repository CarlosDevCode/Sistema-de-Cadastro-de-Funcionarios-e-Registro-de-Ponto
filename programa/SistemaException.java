//Exceção personalizada base do sistema. Todas as demais exceções especificas
//do dominio da aplicacao (cadastro duplicado, funcionario nao encontrado,
//dados invalidos) herdam desta classe.

public class SistemaException extends Exception {

    public SistemaException(String mensagem) {
        super(mensagem);
    }

    public SistemaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
