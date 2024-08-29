package dev.wakandaacademy.produdoro.tarefa.application.api;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.Value;
import java.util.stream.Collectors;

import java.util.List;
import java.util.UUID;

@Value
public class TarefaDetalhadaListResponse {
 
    private UUID idTarefa;
    private String descricao;
    private UUID idUsuario;
    private UUID idArea;
    private UUID idProjeto;
    private StatusTarefa status;
    private StatusAtivacaoTarefa statusAtivacao;
    private int contagemPomodoro;
    private int posicao;

    public TarefaDetalhadaListResponse(Tarefa tarefa) {
        this.idTarefa = tarefa.getIdTarefa();
        this.descricao = tarefa.getDescricao();
        this.idUsuario = tarefa.getIdUsuario();
        this.idArea = tarefa.getIdArea();
        this.idProjeto = tarefa.getIdProjeto();
        this.status = tarefa.getStatus();
        this.statusAtivacao = tarefa.getStatusAtivacao();
        this.contagemPomodoro = tarefa.getContagemPomodoro();
        this.posicao = tarefa.getPosicao();
    }

    public static List<TarefaDetalhadaListResponse> converte(List<Tarefa> tarefas) {
        return tarefas.stream()
            .map(TarefaDetalhadaListResponse::new)
            .collect(Collectors.toList());
    }
}
