package br.com.minispring.CadernoDeReceitas.repository;
import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinaria;
import br.com.minispring.framework.annotation.Repository;
import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinariaInput;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public final class InMemoryReceitaCulinariaRepository implements ReceitaCulinariaRepository {

    private final ConcurrentHashMap<Long, ReceitaCulinaria> Recul = new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong();


    public ReceitaCulinaria create(String nome, float duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, String TutorialPreparo) {
        long id = sequence.incrementAndGet();
        var receitaCulinaria = new ReceitaCulinaria(id,nome,duracaoDoPreparoMinutos,Ingredientes,TutorialPreparo);
        Recul.put(id, receitaCulinaria);
        return receitaCulinaria;

    }

    public List<ReceitaCulinaria> findAll() {
        return Recul.values().stream().sorted(Comparator.comparingLong(ReceitaCulinaria::id)).toList();
    }

    public Optional<ReceitaCulinaria> findById(Long id) {
        return Optional.ofNullable(Recul.get(id));
    }

    public Optional<ReceitaCulinaria> update(long id ,String nome, float duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, String TutorialPreparo) {
        return Optional.ofNullable(Recul.computeIfPresent(id,(key, old) -> new ReceitaCulinaria(id,nome,duracaoDoPreparoMinutos,Ingredientes,TutorialPreparo)));
    }

    public boolean deleteById(Long id) {
        return Recul.remove(id) != null;

    }

}
