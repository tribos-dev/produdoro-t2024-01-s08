package dev.wakandaacademy.produdoro.tarefa.application.api;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TarefaAlteracaoRequest {

    @NotBlank(message = "o campo não pode estar vazio")
    private String descricao;
}