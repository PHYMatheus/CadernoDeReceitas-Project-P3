package br.com.minispring.framework.validation;

import java.lang.annotation.*;

// Exige texto não nulo e com pelo menos um caractere que não seja espaço em branco.
// RECORD_COMPONENT permite ao Validator encontrar a regra em getRecordComponents().
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.RECORD_COMPONENT)
<<<<<<< HEAD
public @interface NotBlank {
}
=======
public @interface NotBlank {}
>>>>>>> e71cef4529f4e7eb37761a681bdd053c4657dd4b
