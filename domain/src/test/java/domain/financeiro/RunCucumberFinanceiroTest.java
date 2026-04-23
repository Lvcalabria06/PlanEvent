package domain.financeiro;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/financeiro",
        glue = "domain.financeiro.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberFinanceiroTest {
}
