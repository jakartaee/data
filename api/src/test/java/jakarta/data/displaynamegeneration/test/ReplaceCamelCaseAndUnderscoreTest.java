package jakarta.data.displaynamegeneration.test;

import jakarta.data.displaynamegeneration.ReplaceCamelCaseAndUnderscore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(ReplaceCamelCaseAndUnderscore.class)
class ReplaceCamelCaseAndUnderscoreTest {
    private ReplaceCamelCaseAndUnderscore replaceCamelCaseAndUnderscore;

    @BeforeEach
    void setUp() {
        replaceCamelCaseAndUnderscore = new ReplaceCamelCaseAndUnderscore();
    }

    @Test
    void shouldManageCamelCaseAndUnderscoreVeryWell() {
        String input1 = "shouldReturnErrorWhen_maxResults_IsNegative";
        String result1 = replaceCamelCaseAndUnderscore.replaceCamelCaseAndUnderscore(input1);
        String input2 = "shouldCreateLimitWithRange";
        String result2 = replaceCamelCaseAndUnderscore.replaceCamelCaseAndUnderscore(input2);
        assertSoftly(softly -> {
            softly.assertThat(result1).isEqualTo("Should return error when maxResults is negative");
            softly.assertThat(result2).isEqualTo("Should create limit with range");
        });
    }
}