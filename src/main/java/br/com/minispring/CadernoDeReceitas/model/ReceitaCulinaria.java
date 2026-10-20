package br.com.minispring.CadernoDeReceitas.model;
import java.util.ArrayList;

public record ReceitaCulinaria(Long id, String nome, float duracaoDoPreparoMinutos, ArrayList<String> Ingredientes, String TutorialPreparo) {}
