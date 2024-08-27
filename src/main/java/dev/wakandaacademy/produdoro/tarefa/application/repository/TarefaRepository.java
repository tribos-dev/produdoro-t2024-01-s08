package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	List<Tarefa> buscaTarefaPorUsuario(UUID idUsuario);
	void deletaTodasTarefas(List<Tarefa> tarefasUsuario);
    void processaStatusEContadorPomodoro(Usuario usuarioPorEmail);
}
