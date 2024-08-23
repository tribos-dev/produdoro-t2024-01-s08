package dev.wakandaacademy.produdoro.tarefa.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;

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
		List<Tarefa> tarefas = tarefaSpringMongoDBRepository.findAllByIdTarefa(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasUsuarioPorId");
		return tarefas;
	}
	
	@Override
	public void modificaOrdemTarefa(Tarefa tarefa, NovaPosicaoRequest novaPosicaoRequest) {
        log.info("[inicia] TarefaInfraRepository - modificaOrdemTarefa");
        List<Tarefa> tarefas = buscaTarefasUsuarioPorId(tarefa.getIdUsuario());
        validaNovaPosicao(tarefas.size(), tarefa.getPosicao(), novaPosicaoRequest.getNovaPosicao());
        int menorPosicao = (novaPosicaoRequest.getNovaPosicao() > tarefa.getPosicao()) ? tarefa.getPosicao() + 1: novaPosicaoRequest.getNovaPosicao();
        int maiorPosicao = (novaPosicaoRequest.getNovaPosicao() < tarefa.getPosicao()) ? novaPosicaoRequest.getNovaPosicao() : tarefa.getPosicao();
        salvaVariasTarefas(novaPosicaoRequest, tarefas, menorPosicao, maiorPosicao);
        log.info("[finaliza] TarefaInfraRepository - modificaOrdemTarefa");
	}
	
	private void salvaVariasTarefas(NovaPosicaoRequest novaPosicaoRequest, List<Tarefa> tarefas, int menorPosicao,
			int maiorPosicao) {
        log.info("[inicia] TarefaInfraRepository - salvaVariasTarefas");
		List<Tarefa> tarefasAtualizadas = IntStream.range(menorPosicao, maiorPosicao)
                .mapToObj(i -> novaPosicaoTarefa(tarefas.get(i), novaPosicaoRequest.getNovaPosicao()))
                .collect(Collectors.toList());
        log.info("[finaliza] TarefaInfraRepository - salvaVariasTarefas");
        tarefaSpringMongoDBRepository.saveAll(tarefasAtualizadas);
	}
	
	private Tarefa novaPosicaoTarefa(Tarefa tarefa, int novaPosicao) {
        log.info("[inicia] TarefaInfraRepository - novaPosicaoTarefa");
        tarefa.editaNovaPosicao(novaPosicao);
        log.info("[finaliza] TarefaInfraRepository - novaPosicaoTarefa");
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
}
