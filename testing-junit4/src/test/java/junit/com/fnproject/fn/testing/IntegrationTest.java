package junit.com.fnproject.fn.testing;

import com.fnproject.fn.testing.FnTestingRule;
import com.fnproject.fn.testing.FunctionError;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

public class IntegrationTest {

    @Rule
    public FnTestingRule fn = FnTestingRule.createDefault();

    @Test
    public void runIntegrationTests() {

        fn.givenFn("nonexistent/nonexistent")
                .withFunctionError()

                .givenFn("appName/route")
                .withAction((body) -> {
                    if (new String(body).equals("PASS")) {
                        return "okay".getBytes();
                    } else {
                        throw new FunctionError("failed as demanded");
                    }
                })
                .givenEvent()
                .withBody("")   // or "1,5,6,32" to select a set of tests individually
                .enqueue()

                .thenRun(ExerciseEverything.class, "handleRequest");

        Assertions.assertThat(fn.getResults().get(0).getBodyAsString())
                  .endsWith("Everything worked\n");
    }
}
