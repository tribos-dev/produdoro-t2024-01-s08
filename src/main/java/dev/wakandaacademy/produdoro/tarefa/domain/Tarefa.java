package dev.wakandaacademy.produdoro.tarefa.domain;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Document(collection = "Tarefa")
public class Tarefa {
	@Id
	private UUID idTarefa;
	@NotBlank
	private String descricao;
	@Indexed
	private UUID idUsuario;
	@Indexed
	private UUID idArea;
	@Indexed
	private UUID idProjeto;
	private StatusTarefa status;
	private StatusAtivacaoTarefa statusAtivacao;
	private int contagemPomodoro;
	private int posicao;

	public Tarefa(TarefaRequest tarefaRequest, int posicao) {
		this.idTarefa = UUID.randomUUID();
		this.idUsuario = tarefaRequest.getIdUsuario();
		this.descricao = tarefaRequest.getDescricao();
		this.idArea = tarefaRequest.getIdArea();
		this.idProjeto = tarefaRequest.getIdProjeto();
		this.status = StatusTarefa.A_FAZER;
		this.statusAtivacao = StatusAtivacaoTarefa.INATIVA;
		this.contagemPomodoro = 1;
		this.posicao = posicao;
	}
	public void pertenceAoUsuario(Usuario usuarioPorEmail) {
		if (!this.idUsuario.equals(usuarioPorEmail.getIdUsuario())) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário não é dono da Tarefa solicitada!");
		}
	}

	public void editaNovaPosicao(int novaPosicao) {
		this.posicao = novaPosicao;
	}
	
	public void defineComoAtiva() {
		if (this.statusAtivacao.equals(StatusAtivacaoTarefa.INATIVA)) {
			statusAtivacao = StatusAtivacaoTarefa.ATIVA;
		}

	}

	public void defineComoInativa() {
		if (this.statusAtivacao.equals(StatusAtivacaoTarefa.ATIVA)) {
			statusAtivacao = StatusAtivacaoTarefa.INATIVA;
		}
	}

	public void concluiTarefa() {
		this.status = StatusTarefa.CONCLUIDA;
	}

	public void incrementaPomodoro(Usuario usuarioPorEmail) {
		if (usuarioPorEmail.getStatus().equals(StatusUsuario.FOCO)) {
			this.contagemPomodoro++;
		}
	}

	public void editaTarefa(TarefaAlteracaoRequest tarefaAlteracaoRequest) {
        this.descricao = tarefaAlteracaoRequest.getDescricao();
    }

    public void atualizaPosicao(int novaPosicao) {
		this.posicao = novaPosicao;
    }
}
