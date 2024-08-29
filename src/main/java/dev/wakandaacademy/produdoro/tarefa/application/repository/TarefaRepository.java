package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);

    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);

    List<Tarefa> buscaTarefaPorUsuario(UUID idUsuario);

    Optional<Tarefa> buscaTarefaJaAtiva(UUID idUsuario);

    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);

    void deletaVariasTarefas(List<Tarefa> tarefasConcluidas);

    List<Tarefa> buscarTarefasPorIdUsuario(UUID idUsuario);

    void processaStatusEContadorPomodoro(Usuario usuarioPorEmail);

    void atualizaPosicaoDasTarefas(List<Tarefa> tarefasDoUsuario);

    int contarTarefas(@NonNull UUID idUsuario);
}
