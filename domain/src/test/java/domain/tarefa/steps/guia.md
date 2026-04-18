# Configurações e Step Definitions (Passos)

Esta pasta (`src/test/java/domain/tarefa/steps`) é destinada ao seu código Java que irá conectar a linguagem textual (Gherkin dos arquivos `.feature`) com a sua lógica de negócio em Java.

## O que colocar nesta pasta:
- Classes Java que atuam como "Step Definitions" (ex: `TarefaSteps.java`).
- Cada classe conterá métodos com as anotações correspondentes do Cucumber (como `@Given`, `@When`, `@Then` / `@Dado`, `@Quando`, `@Entao`).

## O que você faz aqui:
- Você transforma o comportamento que antes era só texto (escrito no arquivo `.feature`) num teste real e automatizado.
- O método em Java será disparado todas as vezes que o passo do Gherkin correspondente for lido no `.feature`.
- É **aqui** que você vai de fato instanciar as suas entidades, como a classe `Tarefa`, invocar os métodos do seu *Domain Model* e usar `Assert` (como `assertEquals`, `assertTrue` do JUnit) para confirmar se o comportamento da entidade está garantido e reflete o cenário planejado de negócio.

**Exemplo:**
```java
public class TarefaSteps {
    private Tarefa tarefaPrincipal;

    @Dado("que a tarefa {string} está em aberto")
    public void a_tarefa_esta_em_aberto(String nomeTarefa) {
        tarefaPrincipal = criarTarefaDeTeste(nomeTarefa);
        // ... sua lógica de setup
    }

    @Quando("eu adiciono a dependência {string} à tarefa")
    public void eu_adiciono_a_dependencia(String nomeDependencia) {
        // ... chamada do seu domain
    }

    @Então("a tarefa principal deve registrar essa nova dependência")
    public void registrar_nova_dependencia() {
        // Assertions garantindo que funcionou
    }
}
```
