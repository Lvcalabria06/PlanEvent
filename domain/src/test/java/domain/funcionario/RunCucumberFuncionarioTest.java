package domain.funcionario;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/funcionario",
        glue = "domain.funcionario.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberFuncionarioTest {
}