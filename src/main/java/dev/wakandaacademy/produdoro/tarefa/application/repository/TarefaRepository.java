package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	void deletaTodasTarefas(List<Tarefa> tarefasUsuario);
	List<Tarefa> buscaTarefaPorUsuario(UUID idUsuario);
	Optional<Tarefa> buscaTarefaJaAtiva(UUID idUsuario);
}
