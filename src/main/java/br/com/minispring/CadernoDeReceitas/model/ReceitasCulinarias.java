package br.com.minispring.CadernoDeReceitas.model;

import br.com.minispring.framework.annotation.Component;

import java.util.ArrayList;
@Component
public class ReceitasCulinarias {
    private String nome;
    private int duracaoDoPreparoMinutos;
    private ArrayList<String> Ingredientes;
    private String tutorialPreparo;

    public ReceitasCulinarias() {
        this.Ingredientes = new ArrayList<>();
        this.tutorialPreparo = "";
        this.duracaoDoPreparoMinutos = 0;
        this.nome = "";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<String> getIngredientes() {
        return Ingredientes;
    }

    public void setIngredientes(ArrayList<String> ingredientes) {
        Ingredientes = ingredientes;
    }

    public int getDuracaoDoPreparoMinutos() {
        return duracaoDoPreparoMinutos;
    }

    public void setDuracaoDoPreparoMinutos(int duracaoDoPreparoMinutos) {
        this.duracaoDoPreparoMinutos = duracaoDoPreparoMinutos;
    }

    public String getTutorialPreparo() {
        return tutorialPreparo;
    }

    public void setTutorialPreparo(String tutorialPreparo) {
        this.tutorialPreparo = tutorialPreparo;
    }
}
