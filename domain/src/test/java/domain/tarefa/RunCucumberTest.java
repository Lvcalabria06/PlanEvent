package domain.tarefa;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/tarefa",
    glue = "domain.tarefa.steps",
    plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberTest {
}
