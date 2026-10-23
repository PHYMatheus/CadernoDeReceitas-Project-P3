package br.com.minispring.CadernoDeReceitas.service;

import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinariaInput;
import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinaria;
import br.com.minispring.CadernoDeReceitas.repository.ReceitaCulinariaRepository;
import br.com.minispring.framework.annotation.Service;
import br.com.minispring.framework.web.HttpException;

import java.util.List;
import java.util.Locale;

@Service
public final class ReceitaCulinariaService {
    private static final int DuracaoMaxima = 1440;
    private final ReceitaCulinariaRepository receitaCulinariaRepository;

    public ReceitaCulinariaService(ReceitaCulinariaRepository receitaCulinariaRepository) {
        this.receitaCulinariaRepository = receitaCulinariaRepository;
    }

    public ReceitaCulinaria create(ReceitaCulinariaInput input) {
        validateBusinessRule(input);
        validateNull(input);
        return receitaCulinariaRepository.create(input.nome().trim(), input.duracaoDoPreparoMinutos(),
                input.Ingredientes(), input.TutorialPreparo());

    }

    public List<ReceitaCulinaria> listByName(String nome) {
        String filter = nome.toLowerCase(Locale.ROOT);
        return receitaCulinariaRepository.findAll().stream().filter(c -> c.nome()
                .toLowerCase(Locale.ROOT).contains(filter)).toList();
    }


    public List<ReceitaCulinaria> listAll() {
        return receitaCulinariaRepository.findAll();
    }

    public ReceitaCulinaria findById(Long id){
        validateID(id);
        return receitaCulinariaRepository.findById(id).orElseThrow(this::notFound);
    }

    public ReceitaCulinaria update(Long id, ReceitaCulinariaInput input) {
        validateID(id);
        validateNull(input);
        validateBusinessRule(input);
        return receitaCulinariaRepository.update(id, input.nome().trim(), input.duracaoDoPreparoMinutos()
                , input.Ingredientes(), input.TutorialPreparo()).orElseThrow(this::notFound);
    }

    public void deleteById(Long id){
        validateID(id);
        if (!receitaCulinariaRepository.deleteById(id)){
            throw notFound();
        }
    }

    // ==== Validações e REGRAS DE NEGÓCIOS. ====

    // Limite Máximo de 1.440 Minutos
    private void validateBusinessRule(ReceitaCulinariaInput input) {
        if (input.duracaoDoPreparoMinutos() > DuracaoMaxima) {
            throw new HttpException(422, "Uma Receita pode ter no Máximo 1.440 Minutos!");
        }
    }

    // Receita Não Encontrada.
    private HttpException notFound() {
        return new HttpException(404, "Receita culinária não encontrada!");
    }

    // Validador de ID
    private void validateID(Long id){
        if (id == null || id <= 0){
            throw  new HttpException(400, "ID invalido!");
        }
    }

    // Não Aceita inputs vazios
    private void validateNull(ReceitaCulinariaInput input) {
        if (input == null) {
            throw new HttpException(400, "Dados da Receita são obrigatórios!");
        }
    }
}
