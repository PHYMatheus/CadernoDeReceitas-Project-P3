package br.com.minispring.CadernoDeReceitas.model;
import java.util.ArrayList;

public record ReceitaCulinaria(Long id, String nome, int duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, String TutorialPreparo) {}
