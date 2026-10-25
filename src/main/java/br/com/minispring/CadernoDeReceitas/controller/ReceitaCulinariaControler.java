package br.com.minispring.CadernoDeReceitas.controller;

import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinaria;
import br.com.minispring.CadernoDeReceitas.model.ReceitaCulinariaInput;
import br.com.minispring.CadernoDeReceitas.service.ReceitaCulinariaService;
import br.com.minispring.framework.annotation.PathVariable;
import br.com.minispring.framework.annotation.RequestBody;
import br.com.minispring.framework.annotation.RequestParam;
import br.com.minispring.framework.annotation.RestController;
import br.com.minispring.framework.annotation.Route;
import br.com.minispring.framework.web.HttpException;
import br.com.minispring.framework.web.HttpResult;

import java.util.List;

@RestController("/receitas")
public final class ReceitaCulinariaControler {
    private final ReceitaCulinariaService service;

    public ReceitaCulinariaControler(ReceitaCulinariaService service) {
        this.service = service;
    }

    @Route(method = "POST")
    public HttpResult criar(@RequestBody ReceitaCulinariaInput input) {
        ReceitaCulinaria criada = service.create(input);
        return HttpResult.created("/receitas/" + criada.id(), criada);
    }

    @Route(method = "GET")
    public List<ReceitaCulinaria> listar(@RequestParam("nome") String nome) {
        if (nome == null || nome.isBlank()) {
            return service.listAll();
        }
        return service.listByName(nome);
    }

    @Route(method = "GET", path = "/{id}")
    public ReceitaCulinaria buscarPorId(@PathVariable("id") String id) {
        return service.findById(parseId(id));
    }

    @Route(method = "PUT", path = "/{id}")
    public ReceitaCulinaria atualizar(@PathVariable("id") String id, @RequestBody ReceitaCulinariaInput input) {
        return service.update(parseId(id), input);
    }

    @Route(method = "DELETE", path = "/{id}")
    public HttpResult excluir(@PathVariable("id") String id) {
        service.deleteById(parseId(id));
        return HttpResult.noContent();
    }

    // Erros 400/404/422 vêm do service e o DispatcherServlet converte em resposta HTTP.
    private Long parseId(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new HttpException(400, "O ID da receita deve ser um número inteiro (ex: /receitas/1)!");
        }
    }
}
