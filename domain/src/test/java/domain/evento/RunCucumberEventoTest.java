package domain.evento;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/evento",
        glue = "domain.evento.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberEventoTest {
}
