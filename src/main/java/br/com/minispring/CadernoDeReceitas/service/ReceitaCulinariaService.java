package br.com.minispring.CadernoDeReceitas.service;

import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinariaInput;
import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinaria;
import br.com.minispring.CadernoDeReceitas.repository.ReceitaCulinariaRepository;
import br.com.minispring.framework.annotation.Service;
import br.com.minispring.framework.web.HttpException;

import java.util.ArrayList;
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
        validateReceita(input);
        return receitaCulinariaRepository.create(input.nome().trim(), input.duracaoDoPreparoMinutos(),
                cleanIngredients(input.Ingredientes()), input.TutorialPreparo().trim());
    }

    public List<ReceitaCulinaria> listByName(String nome) {
        String filter = nome == null ? "" : nome.trim().toLowerCase(Locale.ROOT);
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
        validateReceita(input);
        return receitaCulinariaRepository.update(id, input.nome().trim(), input.duracaoDoPreparoMinutos(),
                cleanIngredients(input.Ingredientes()), input.TutorialPreparo().trim()).orElseThrow(this::notFound);
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

    // Receita Não Encontrada
    private HttpException notFound() {
        return new HttpException(404, "Receita culinária não encontrada!");
    }

    // Validador de ID
    private void validateID(Long id){
        if (id == null || id <= 0){
            throw  new HttpException(400, "ID inválido!");
        }
    }

    // Não Aceita inputs vazios
    private void validateNull(ReceitaCulinariaInput input) {
        if (input == null) {
            throw new HttpException(400, "Dados da Receita são obrigatórios!");
        }
    }

    // Aqui a lista não vai guardar ingredientes por referência.
    private ArrayList<String> cleanIngredients(List<String> ingredientes) {
        return new ArrayList<>(ingredientes.stream().map(String::trim).toList());
    }

    // Duração maior que zero.
    private void validatePositive(float duracaoDoPreparoMinutos) {
        if (!(duracaoDoPreparoMinutos > 0)) {
            throw new HttpException(400, "duracaoDoPreparoMinutos deve ser maior que zero!");
        }
    }

    // Texto obrigatório: não aceita null, vazio nem só espaços.
    private void validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new HttpException(400, field + " não pode estar em branco!");
        }
    }

    // Pelo menos um ingrediente e nenhum em branco.
    private void validateIngredients(List<String> ingredientes) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            throw new HttpException(400, "Ingredientes deve ter pelo menos um item!");
        }
        if (ingredientes.stream().anyMatch(i -> i == null || i.isBlank())) {
            throw new HttpException(400, "Ingredientes não pode ter item em branco!");
        }
    }

    // Aqui precisei juntar todas as validações em ordem para funcionar tanto no Create como no Update.
    private void validateReceita(ReceitaCulinariaInput input) {
        validateNull(input);
        validateText(input.nome(), "nome");
        validateText(input.TutorialPreparo(), "TutorialPreparo");
        validateIngredients(input.Ingredientes());
        validatePositive(input.duracaoDoPreparoMinutos());
        validateBusinessRule(input);
    }
}
