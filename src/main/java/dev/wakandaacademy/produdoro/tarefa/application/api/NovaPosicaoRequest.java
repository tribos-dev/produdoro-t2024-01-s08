package dev.wakandaacademy.produdoro.tarefa.application.api;

import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NovaPosicaoRequest {
	@PositiveOrZero(message = "Posição deve ser maior ou igual a zero.")
	private int novaPosicao;
}