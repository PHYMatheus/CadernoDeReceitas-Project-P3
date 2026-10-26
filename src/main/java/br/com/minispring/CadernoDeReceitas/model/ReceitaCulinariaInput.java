package br.com.minispring.CadernoDeReceitas.model;

import br.com.minispring.framework.validation.NotBlank;
import br.com.minispring.framework.validation.Positive;

import java.util.ArrayList;

public record ReceitaCulinariaInput(@NotBlank String nome, @Positive float duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, @NotBlank String TutorialPreparo) {
}
