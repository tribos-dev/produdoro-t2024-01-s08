package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);
    void editaTarefa(String email, UUID idTarefa, TarefaAlteracaoRequest tarefaAlteracaoRequest);
    void modificaOrdemTarefa(String emailUsuario, NovaPosicaoRequest novaPosicaoRequest, UUID idTarefa);
	void deletaTodasTarefas(String emailUsuario, UUID idUsuario);
    void defineTarefaComoAtiva(UUID idTarefa, String usuarioEmail);
    void deletaTarefasConcluidas(String email, UUID idUsuario);
    void concluiTarefa(String email, UUID idTarefa);
    void incrementaPomodoro(String usuario, UUID idTarefa);
}
