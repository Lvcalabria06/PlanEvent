package domain.local;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/local",
        glue = "domain.local.steps",
        plugin = {"pretty"}
)
@SuppressWarnings("deprecation")
public class RunCucumberLocalTest {
}
