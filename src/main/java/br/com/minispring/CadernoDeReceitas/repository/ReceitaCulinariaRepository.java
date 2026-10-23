package br.com.minispring.CadernoDeReceitas.repository;

import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinaria;
import br.com.minispring.framework.annotation.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceitaCulinariaRepository {
    ReceitaCulinaria create(String nome, int duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, String TutorialPreparo);

    List<ReceitaCulinaria> findAll();

    Optional<ReceitaCulinaria> findById(Long id);

    Optional<ReceitaCulinaria> update(Long id, String nome, int duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, String TutorialPreparo);

    boolean deleteById(Long id);

}
