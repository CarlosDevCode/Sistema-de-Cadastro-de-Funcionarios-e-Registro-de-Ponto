import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//Representa um registro de ponto (entrada e saida) de um funcionario CLT
//em um determinado dia.
public class RegistroPonto implements Serializable {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private LocalDate data;
    private LocalTime horaEntrada;
    private LocalTime horaSaida;

    public RegistroPonto(LocalDate data, LocalTime horaEntrada) {
        this.data = data;
        this.horaEntrada = horaEntrada;
        this.horaSaida = null;
    }

    // Sobrecarga de construtor: usado ao carregar registros ja fechados do arquivo
    public RegistroPonto(LocalDate data, LocalTime horaEntrada, LocalTime horaSaida) {
        this.data = data;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public boolean isFechado() {
        return horaSaida != null;
    }

    public double getHorasTrabalhadas() {
        if (horaSaida == null) {
            return 0.0;
        }
        Duration duracao = Duration.between(horaEntrada, horaSaida);
        return duracao.toMinutes() / 60.0;
    }

    public String toFileString() {
        String saida = (horaSaida == null) ? "null" : horaSaida.format(FORMATO_HORA);
        return data + ";" + horaEntrada.format(FORMATO_HORA) + ";" + saida;
    }

    public static RegistroPonto fromFileString(String linha) {
        String[] partes = linha.split(";");
        LocalDate data = LocalDate.parse(partes[0]);
        LocalTime entrada = LocalTime.parse(partes[1]);
        LocalTime saida = partes[2].equals("null") ? null : LocalTime.parse(partes[2]);
        return new RegistroPonto(data, entrada, saida);
    }

    @Override
    public String toString() {
        String saidaStr = (horaSaida == null) ? "(em aberto)" : horaSaida.toString();
        return String.format("Data: %s | Entrada: %s | Saida: %s | Horas trabalhadas: %.2f",
                data, horaEntrada, saidaStr, getHorasTrabalhadas());
    }
}
