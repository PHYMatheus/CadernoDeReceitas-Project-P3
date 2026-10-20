package br.com.minispring.CadernoDeReceitas.dto;

import br.com.minispring.framework.validation.NotBlank;

import java.util.ArrayList;

public record ReceitasCulinariasResponseDTO(
        String nome,
        int duracaoDoPreparoMinutos,
        ArrayList<String> Ingredientes,
        String tutorialPreparo




) {


}
