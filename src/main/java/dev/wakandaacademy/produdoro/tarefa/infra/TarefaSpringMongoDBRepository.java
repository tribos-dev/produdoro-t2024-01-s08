package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.Query;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;

public interface TarefaSpringMongoDBRepository extends MongoRepository<Tarefa, UUID> {
    Optional<Tarefa> findByIdTarefa(UUID idTarefa);
	List<Tarefa> findAllByIdUsuarioOrderByPosicaoAsc(UUID idUsuario);
    List<Tarefa> findAllByIdUsuario(UUID idUsuario);
    @Query("{ 'statusAtivacao' : ?0, 'idUsuario' : ?1 }")
    Optional<Tarefa> buscaTarefaJaAtiva(StatusAtivacaoTarefa statusAtivacaoTarefa, UUID idUsuario);
    List<Tarefa> findAllByIdUsuarioOrderByPosicao(UUID idUsuario);
}
