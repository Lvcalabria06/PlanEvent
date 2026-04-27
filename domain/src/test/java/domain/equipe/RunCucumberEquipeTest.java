package domain.equipe;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/equipe",
        glue = "domain.equipe.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberEquipeTest {
}