package br.com.minispring.CadernoDeReceitas.repository;

import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinaria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ReceitaCulinariaRepository {
    ReceitaCulinaria create(String nome, float duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, String TutorialPreparo);

    List<ReceitaCulinaria> findAll();

    Optional<ReceitaCulinaria> findById(Long id);

    Optional<ReceitaCulinaria> update(long id,String nome,float duracaoDoPreparoMinutos, ArrayList<String> Ingredientes,String TutorialPreparo);

    boolean deleteById(Long id);

}
