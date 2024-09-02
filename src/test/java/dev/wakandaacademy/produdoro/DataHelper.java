package dev.wakandaacademy.produdoro;

import java.util.List;
import java.util.UUID;

import dev.wakandaacademy.produdoro.pomodoro.domain.ConfiguracaoPadrao;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

public class DataHelper {

    private static final UUID usuario1 = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");

    public static Usuario createUsuario() {
        return Usuario.builder().email("email@email.com").status(StatusUsuario.PAUSA_CURTA).idUsuario(usuario1).build();
    }
    public static Usuario createUsuario1() {
        return Usuario.builder().email("email@email.com").status(StatusUsuario.PAUSA_LONGA).idUsuario(usuario1).build();
    }

    public static Tarefa createTarefa() {
        return Tarefa.builder().contagemPomodoro(1).idTarefa(UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb"))
                .idUsuario(usuario1).descricao("descricao tarefa").statusAtivacao(StatusAtivacaoTarefa.INATIVA).build();
    }

    public static UsuarioNovoRequest getUsuarioRequest() {
        UsuarioNovoRequest userReq = new UsuarioNovoRequest("email@email.com", "12345");
        return userReq;
    }

    public static ConfiguracaoPadrao getConfig() {
        return ConfiguracaoPadrao.builder().tempoMinutosFoco(25).tempoMinutosPausaCurta(5).tempoMinutosPausaLonga(15)
                .repeticoesParaPausaLonga(3).build();
    }

    public static TarefaRequest getTarefaRequest() {
        Usuario usuario = createUsuario();

        TarefaRequest tarefaReq = new TarefaRequest("descricao", usuario.getIdUsuario(), null, null, 1);
        return tarefaReq;
    }

    public static List<Tarefa> createListTarefaVazia() {
        return List.of();
    }

    public static List<Tarefa> createListTarefa() {
        return List.of(Tarefa.builder().idTarefa(UUID.randomUUID()).descricao("tarefa 1").idUsuario(usuario1).build(),
                Tarefa.builder().build(),
                Tarefa.builder().idTarefa(UUID.randomUUID()).descricao("tarefa 2").idUsuario(usuario1).build(),
                Tarefa.builder().build(),
                Tarefa.builder().idTarefa(UUID.randomUUID()).descricao("tarefa 3").idUsuario(usuario1).build(),
                Tarefa.builder().build(),
                Tarefa.builder().idTarefa(UUID.randomUUID()).descricao("tarefa 4").idUsuario(usuario1).build(),
                Tarefa.builder().build()

        );
    }

    public static List<Tarefa> createTarefasConcluidas() {
        return List.of(Tarefa.builder().idTarefa(UUID.randomUUID()).descricao("tarefa 1").idUsuario(usuario1).status(StatusTarefa.CONCLUIDA).build(),
                Tarefa.builder().idTarefa(UUID.randomUUID()).descricao("tarefa 2").idUsuario(usuario1).status(StatusTarefa.CONCLUIDA).build(),
                Tarefa.builder().idTarefa(UUID.randomUUID()).descricao("tarefa 3").idUsuario(usuario1).status(StatusTarefa.CONCLUIDA).build());
    }
}