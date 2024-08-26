package dev.wakandaacademy.produdoro.tarefa.application.repository;

import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

public interface TarefaRepository {
    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	int novaPosicao(UUID idUsuario);
	void modificaOrdemTarefa(Tarefa tarefa, NovaPosicaoRequest novaPosicaoRequest);
    void processaStatusEContadorPomodoro(Usuario usuarioPorEmail);
}
