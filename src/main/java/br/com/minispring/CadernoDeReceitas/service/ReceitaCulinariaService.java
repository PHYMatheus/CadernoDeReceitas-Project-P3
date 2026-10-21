package br.com.minispring.CadernoDeReceitas.service;

import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinariaInput;
import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinaria;
import br.com.minispring.CadernoDeReceitas.repository.ReceitaCulinariaRepository;
import br.com.minispring.framework.annotation.Service;
import br.com.minispring.framework.web.HttpException;

import java.util.List;

@Service
public final class ReceitaCulinariaService {
    private final ReceitaCulinariaRepository receitaCulinariaRepository;

    public ReceitaCulinariaService(ReceitaCulinariaRepository receitaCulinariaRepository) {
        this.receitaCulinariaRepository = receitaCulinariaRepository;
    }

    public ReceitaCulinaria create(ReceitaCulinariaInput input) {
        return receitaCulinariaRepository.create(input.nome().trim(), input.duracaoDoPreparoMinutos(),
                input.Ingredientes(), input.TutorialPreparo());
    }

    public List<ReceitaCulinaria> ListAll() {
        return receitaCulinariaRepository.findAll();
    }

    public void findById(){
    }

    public void update(long id, ReceitaCulinariaInput input) {
        validarID(id);


    }
    public void deleteById(long id){
        validarID(id);
        if (!receitaCulinariaRepository.deleteById(id)){
            throw notFound();
        }
    }

    // ==== Validações e REGRAS DE NEGÓCIOS. ====


    // Limite Máximo de 1.440 Minutos
    private void validateBusinessRule(ReceitaCulinaria input) {
        if (input.duracaoDoPreparoMinutos() > 1440) throw new HttpException(422, "Uma Receita pode ter no Máximo 1.440 Minutos!");
    }

    // Receita Não Encontrada.
    private HttpException notFound() {
        return new HttpException(404, "Receita culinária não encontrada");
    }

    // Validador de ID
    private void validarID (Long id){
        if (id == null || id < 0){
            throw  new HttpException(400, "ID invalido!");
        }
    }
}
