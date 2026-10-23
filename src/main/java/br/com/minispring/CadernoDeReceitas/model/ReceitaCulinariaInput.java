package br.com.minispring.CadernoDeReceitas.model;

import br.com.minispring.framework.validation.NotBlank;

import java.util.ArrayList;

public record ReceitaCulinariaInput(@NotBlank String nome, @NotBlank float duracaoDoPreparoMinutos, @NotBlank ArrayList<String> Ingredientes, @NotBlank String TutorialPreparo) {
}
