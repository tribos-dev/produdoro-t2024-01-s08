package dev.wakandaacademy.produdoro.tarefa.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public List<Tarefa> buscaTarefaPorUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorUsuario");
        List<Tarefa> buscaTodasTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorUsuario");
        return buscaTodasTarefas;
    }

    @Override
    public Optional<Tarefa> buscaTarefaJaAtiva(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaJaAtiva");
        Optional<Tarefa> tarefaJaAtiva = tarefaSpringMongoDBRepository
                .buscaTarefaJaAtiva(StatusAtivacaoTarefa.ATIVA, idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaJaAtiva");
        return tarefaJaAtiva;
    }

    @Override
    public List<Tarefa> buscaTarefasConcluidas(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefasConcluidas");
        Query query = new Query();
        query.addCriteria(Criteria.where("idUsuario").is(idUsuario).and("status").is(StatusTarefa.CONCLUIDA));
        List<Tarefa> tarefasConcluidas = mongoTemplate.find(query, Tarefa.class);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasConcluidas");
        return tarefasConcluidas;
    }

    @Override
    public void deletaVariasTarefas(List<Tarefa> tarefasConcluidas) {
        log.info("[inicia] TarefaInfraRepository - deletaVariasTarefas");
        tarefaSpringMongoDBRepository.deleteAll(tarefasConcluidas);
        log.info("[finaliza] TarefaInfraRepository - deletaVariasTarefas");
    }

    @Override
    public List<Tarefa> buscarTarefasPorIdUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscarTarefasPorIdUsuario");
        List<Tarefa> todasTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuarioOrderByPosicao(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscarTarefasPorIdUsuario");
        return todasTarefas;
    }

    @Override
    public void processaStatusEContadorPomodoro(Usuario usuarioPorEmail) {
        log.info("[inicia] - TarefaInfraRepository - processaStatusEContadorPomodoro");
        if (usuarioPorEmail.getStatus().equals(StatusUsuario.FOCO)) {
            if (this.contagemPomodoroPausaCurta < 3) {
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

    @Override
    public void atualizaPosicaoDasTarefas(List<Tarefa> tarefasDoUsuario) {
        log.info("[inicia] TarefaInfraRepository - atualizaPosicaoDasTarefas");
        int tamanhoDaLista = tarefasDoUsuario.size();
        List<Tarefa> tarefasAtualizadas = IntStream.range(0, tamanhoDaLista)
                .mapToObj(i -> atualizaTarefaComNovaPosicao(tarefasDoUsuario.get(i), i)).collect(Collectors.toList());
        salvaVariasTarefas(tarefasAtualizadas);
        log.info("[finaliza] TarefaInfraRepository - atualizaPosicaoDasTarefas");
    }

    private void salvaVariasTarefas(List<Tarefa> tarefasDoUsuario) {
        tarefaSpringMongoDBRepository.saveAll(tarefasDoUsuario);
    }

    @Override
    public int contarTarefas(UUID idUsuario) {
        List<Tarefa> tarefasDoUsuario = buscarTarefasPorIdUsuario(idUsuario);
        int novaPosicao = tarefasDoUsuario.size();
        return novaPosicao;
    }

    private Tarefa atualizaTarefaComNovaPosicao(Tarefa tarefa, int novaPosicao) {
        tarefa.atualizaPosicao(novaPosicao);
        return tarefa;
    }
}
