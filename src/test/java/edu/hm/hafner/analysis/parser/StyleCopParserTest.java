package edu.hm.hafner.analysis.parser;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.AbstractParser;
import edu.hm.hafner.analysis.AbstractParserTest;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Issues;
import edu.hm.hafner.analysis.Priority;
import static edu.hm.hafner.analysis.assertj.Assertions.*;
import edu.hm.hafner.analysis.assertj.SoftAssertions;

/**
 * Tests the class {@link StyleCopParser}.
 *
 * @author Sebastian Seidl
 */
public class StyleCopParserTest extends AbstractParserTest {
    StyleCopParserTest() {
        super("stylecop.xml");
    }

    @Override
    protected AbstractParser createParser() {
        return new StyleCopParser();
    }

    @Override
    protected void assertThatIssuesArePresent(final Issues<Issue> issues, final SoftAssertions softly) {
        assertThat(issues).hasSize(5);

        Iterator<Issue> iterator = issues.iterator();
        softly.assertThat(iterator.next())
                .hasPriority(Priority.NORMAL)
                .hasCategory("ReadabilityRules")
                .hasLineStart(18)
                .hasLineEnd(18)
                .hasMessage(
                        "The call to components must begin with the 'this.' prefix to indicate that the item is a member of the class.")
                .hasFileName("Form1.Designer.cs");
        softly.assertThat(iterator.next())
                .hasPriority(Priority.NORMAL)
                .hasCategory("ReadabilityRules")
                .hasLineStart(16)
                .hasLineEnd(16)
                .hasMessage(
                        "The call to components must begin with the 'this.' prefix to indicate that the item is a member of the class.")
                .hasFileName("Form1.Designer.cs");
        softly.assertThat(iterator.next())
                .hasPriority(Priority.NORMAL)
                .hasCategory("DocumentationRules")
                .hasLineStart(7)
                .hasLineEnd(7)
                .hasMessage("The class must have a documentation header.")
                .hasFileName("MainClass.cs");
        softly.assertThat(iterator.next())
                .hasPriority(Priority.NORMAL)
                .hasCategory("DocumentationRules")
                .hasLineStart(9)
                .hasLineEnd(9)
                .hasMessage("The field must have a documentation header.")
                .hasFileName("MainClass.cs");
        softly.assertThat(iterator.next())
                .hasPriority(Priority.NORMAL)
                .hasCategory("DocumentationRules")
                .hasLineStart(10)
                .hasLineEnd(10)
                .hasMessage("The property must have a documentation header.")
                .hasFileName("MainClass.cs");
    }

    /**
     * Verifies that the StyleCop parser works as expected with a file of 3 warnings.
     */
    @Test
    public void testStyleCopOneFile() {
        Issues<Issue> result = parse("stylecop/onefile.xml");

        assertThat(result).hasSize(3);
    }

    /**
     * Verifies that the StyleCop parser works as expected with a file of 2 warnings (4.3 format).
     */
    @Test
    public void testStyleCop43() {
        Issues<Issue> result = parse("stylecop/stylecop-v4.3.xml");

        assertThat(result).hasSize(2);
    }
}

