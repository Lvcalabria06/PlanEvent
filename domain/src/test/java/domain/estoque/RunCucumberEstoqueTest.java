package domain.estoque;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/estoque",
        glue = "domain.estoque.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberEstoqueTest {
}
