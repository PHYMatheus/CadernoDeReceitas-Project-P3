package br.com.minispring.CadernoDeReceitas.dto;

import br.com.minispring.framework.annotation.RequestBody;
import br.com.minispring.framework.validation.NotBlank;

import java.util.ArrayList;

public record ReceitasCulinariasRequestDTO(
        @NotBlank
        String nome,

        @NotBlank
        int duracaoDoPreparoMinutos,

        @NotBlank
        ArrayList<String>Ingredientes,

        @NotBlank
        String tutorialPreparo
) {
}
