package dev.wakandaacademy.produdoro.tarefa.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;
    private final MongoTemplate mongoTemplate;
    private Integer contagemPomodoroPausaCurta = 0;

    @Override
    public Tarefa salva(Tarefa tarefa) {
        log.info("[inicia] TarefaInfraRepository - salva");
        try {
            tarefaSpringMongoDBRepository.save(tarefa);
        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
        }
        log.info("[finaliza] TarefaInfraRepository - salva");
        return tarefa;
    }
    @Override
    public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorId");
        Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorId");
        return tarefaPorId;
    }
    
	@Override
	public int novaPosicao(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - novaPosicao");
        List<Tarefa> tarefas = buscaTarefasUsuarioPorId(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - novaPosicao");
		return tarefas.size();
	}
	
	private List<Tarefa> buscaTarefasUsuarioPorId(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefasUsuarioPorId");
		List<Tarefa> tarefas = tarefaSpringMongoDBRepository.findAllByIdUsuarioOrderByPosicaoAsc(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasUsuarioPorId");
		return tarefas;
	}
	
	@Override
	public void modificaOrdemTarefa(Tarefa tarefa, NovaPosicaoRequest novaPosicaoRequest) {
        log.info("[inicia] TarefaInfraRepository - modificaOrdemTarefa");
        List<Tarefa> tarefas = buscaTarefasUsuarioPorId(tarefa.getIdUsuario());
        validaNovaPosicao(tarefas.size(), tarefa.getPosicao(), novaPosicaoRequest.getNovaPosicao());
        int menorPosicao = Math.min(tarefa.getPosicao(), novaPosicaoRequest.getNovaPosicao());
        int maiorPosicao = Math.max(tarefa.getPosicao(), novaPosicaoRequest.getNovaPosicao());
        salvaVariasTarefas(tarefas, tarefa.getPosicao(), novaPosicaoRequest.getNovaPosicao(), menorPosicao, maiorPosicao);
        log.info("[finaliza] TarefaInfraRepository - modificaOrdemTarefa");
	}
	
	private void salvaVariasTarefas(List<Tarefa> tarefas, int origem, int destino, int menorPosicao, int maiorPosicao) {        
		log.info("[inicia] TarefaInfraRepository - salvaVariasTarefas");
		List<Tarefa> tarefasAtualizadas = IntStream.range(menorPosicao, maiorPosicao)
                .mapToObj(posicao -> {
                    return novaPosicaoTarefa(tarefas, origem, destino, posicao);
                })
                .collect(Collectors.toList());
        log.info("[finaliza] TarefaInfraRepository - salvaVariasTarefas");
        tarefaSpringMongoDBRepository.saveAll(tarefasAtualizadas);
	}
	
	private Tarefa novaPosicaoTarefa(List<Tarefa> tarefas, int origem, int destino, int posicao) {
        log.info("[inicia] TarefaInfraRepository - novaPosicaoTarefa");
		Tarefa tarefa = destino < origem  ? atualizaTarefa(tarefas.get(posicao), posicao + 1) :  atualizaTarefa(tarefas.get(posicao + 1), posicao);
        log.info("[finaliza] TarefaInfraRepository - novaPosicaoTarefa");
		return tarefa;
	}
	
	private Tarefa atualizaTarefa(Tarefa tarefa, int novaPosicao) {
        log.info("[inicia] TarefaInfraRepository - atualizaTarefa");
        tarefa.editaNovaPosicao(novaPosicao);
        log.info("[finaliza] TarefaInfraRepository - atualizaTarefa");
		return tarefa;
	}
	
	private void validaNovaPosicao(int tamanhoLista, int posicaoOrigem, int novaPosicao) {
        log.info("[inicia] TarefaInfraRepository - validaNovaPosicao");
        Optional.of(posicaoOrigem)
        	.filter(posicao -> posicao >= 0 && posicao < tamanhoLista)
        	.filter(posicao -> posicao != novaPosicao)
        	.orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Posição inválida."));
        log.info("[finaliza] TarefaInfraRepository - validaNovaPosicao");
	}

    @Override
    public void processaStatusEContadorPomodoro(Usuario usuarioPorEmail) {
        log.info("[inicia] - TarefaInfraRepository - processaStatusEContadorPomodoro");
        if(usuarioPorEmail.getStatus().equals(StatusUsuario.FOCO)){
            if (this.contagemPomodoroPausaCurta < 3){
                usuarioPorEmail.mudaStatusPausaCurta();
            } else {
                usuarioPorEmail.mudaStatusPausaLonga();
                this.contagemPomodoroPausaCurta = 0;
            }
        } else {
            usuarioPorEmail.alteraStatusParaFoco(usuarioPorEmail.getIdUsuario());
            this.contagemPomodoroPausaCurta++;
        }
        Query query = Query.query(Criteria.where("idUsuario").is(usuarioPorEmail.getIdUsuario()));
        Update updateUsuario = Update.update("status", usuarioPorEmail.getStatus());
        mongoTemplate.updateMulti(query, updateUsuario, Usuario.class);
        log.info("[finaliza] - TarefaInfraRepository - processaStatusEContadorPomodoro");
    }
}
