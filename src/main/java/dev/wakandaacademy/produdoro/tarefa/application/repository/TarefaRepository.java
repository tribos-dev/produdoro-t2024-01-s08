package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;

public interface TarefaRepository {
    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	int novaPosicao(UUID idUsuario);
	void modificaOrdemTarefa(Tarefa tarefa, NovaPosicaoRequest novaPosicaoRequest);
	List<Tarefa> buscaTarefaPorUsuario(UUID idUsuario);
	void deletaTodasTarefas(List<Tarefa> tarefasUsuario);
    void processaStatusEContadorPomodoro(Usuario usuarioPorEmail);
    Optional<Tarefa> buscaTarefaJaAtiva(UUID idUsuario);
    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);
    void deletaVariasTarefas(List<Tarefa> tarefasConcluidas);
    List<Tarefa> buscarTarefasPorIdUsuario(UUID idUsuario);
    void atualizaPosicaoDasTarefas(List<Tarefa> tarefasDoUsuario);
    int contarTarefas(@NonNull UUID idUsuario);
}
