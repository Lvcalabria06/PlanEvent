package domain.agenda;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/agenda",
        glue = "domain.agenda.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberAgendaTest {
}
