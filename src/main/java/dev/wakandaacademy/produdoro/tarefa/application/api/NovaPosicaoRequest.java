package dev.wakandaacademy.produdoro.tarefa.application.api;

import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NovaPosicaoRequest {
	@PositiveOrZero(message = "Posição deve ser maior ou igual a zero.")
	private int novaPosicao;
}