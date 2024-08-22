package dev.wakandaacademy.produdoro.tarefa.application.repository;

import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	int novaPosicao(UUID idUsuario);
	void mudaOrdemTarefa(Tarefa terefa, NovaPosicaoRequest novaPosicaoRequest);
}
