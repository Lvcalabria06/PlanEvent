package domain.contrato;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/contrato/contrato.feature",
        glue = "domain.contrato.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberContratoTest {
}
