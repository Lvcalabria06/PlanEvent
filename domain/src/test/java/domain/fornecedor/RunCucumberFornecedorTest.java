package domain.fornecedor;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/fornecedor",
        glue = "domain.fornecedor.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberFornecedorTest {
}
