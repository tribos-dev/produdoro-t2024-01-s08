package dev.wakandaacademy.produdoro.tarefa.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

public interface TarefaSpringMongoDBRepository extends MongoRepository<Tarefa, UUID> {
    Optional<Tarefa> findByIdTarefa(UUID idTarefa);
	List<Tarefa> findAllByIdUsuario(UUID idUsuario);
	
    @Query("{ 'statusAtivacao' : ?0, 'idUsuario' : ?1 }")
	Optional<Tarefa> buscaTarefaJaAtiva(StatusAtivacaoTarefa statusAtivacaoTarefa, UUID idUsuario);
}
