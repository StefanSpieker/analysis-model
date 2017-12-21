package edu.hm.hafner.analysis.parser;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.AbstractParser;
import edu.hm.hafner.analysis.AbstractParserTest;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Issues;
import edu.hm.hafner.analysis.Priority;
import static edu.hm.hafner.analysis.assertj.Assertions.*;
import edu.hm.hafner.analysis.assertj.SoftAssertions;
import static edu.hm.hafner.analysis.assertj.SoftAssertions.*;

/**
 * Tests the class {@link AntJavacParser}.
 */
public class AntJavacParserTest extends AbstractParserTest {
    /**
     * Creates a new instance of {@link AbstractParserTest}.
     *
     */
    protected AntJavacParserTest() {
        super("ant-javac.txt");
    }

    /**
     * Parses a warning log with two warnings.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-24611">Issue 24611</a>
     */
    @Test
    public void testIssue24611() {
        Issues<Issue> warnings = parse("issue24611.txt");

        assertThat(warnings).hasSize(2);
    }

    /**
     * Parses a warning log with one warning that refers to a missing class file.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-21240">Issue 21240</a>
     */
    @Test
    public void issue21240() {
        Issues<Issue> warnings = parse("issue21240.txt");

        assertThat(warnings).hasSize(1);

        assertSoftly(softly -> {
            softly.assertThat(warnings.get(0))
                    .hasPriority(Priority.NORMAL)
                    .hasLineStart(0)
                    .hasLineEnd(0)
                    .hasMessage("Cannot find annotation method 'xxx()' in type 'yyyy': class file for fully.qualified.ClassName not found")
                    .hasFileName("aaa.class");
        });
    }

    /**
     * Parses a warning log with 2 ANT warnings.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-2133">Issue 2133</a>
     */
    @Test
    public void issue2133() {
        Issues<Issue> warnings = parse("issue2133.txt");

        assertThat(warnings).hasSize(2);

        assertSoftly(softly -> {
            softly.assertThat(warnings.get(0))
                    .hasPriority(Priority.NORMAL)
                    .hasCategory(DEFAULT_CATEGORY)
                    .hasLineStart(86)
                    .hasLineEnd(86)
                    .hasMessage("non-varargs call of varargs method with inexact argument type for last parameter;")
                    .hasFileName("/home/hudson/hudson/data/jobs/Mockito/workspace/trunk/test/org/mockitousage/misuse/DescriptiveMessagesOnMisuseTest.java");
            softly.assertThat(warnings.get(1))
                    .hasPriority(Priority.NORMAL)
                    .hasCategory(AbstractParser.DEPRECATION)
                    .hasLineStart(51)
                    .hasLineEnd(51)
                    .hasMessage("<T>stubVoid(T) in org.mockito.Mockito has been deprecated")
                    .hasFileName("/home/hudson/hudson/data/jobs/Mockito/workspace/trunk/test/org/mockitousage/stubbing/StubbingWithThrowablesTest.java");
            softly.assertAll();
        });
    }

    /**
     * Parses a warning log with 1 warnings that has no associated file.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-4098">Issue 4098</a>
     */
    @Test
    public void issue4098() {
        Issues<Issue> warnings = parse("issue4098.txt");

        assertThat(warnings).hasSize(1);

        assertSoftly(softly -> {
            softly.assertThat(warnings.get(0))
                    .hasPriority(Priority.NORMAL)
                    .hasCategory("Path")
                    .hasLineStart(0)
                    .hasLineEnd(0)
                    .hasMessage("bad path element \"C:\\...\\.hudson\\jobs\\...\\log4j.jar\": no such file or directory")
                    .hasFileName("C:/.../.hudson/jobs/.../log4j.jar");
        });
    }

    /**
     * Parses a warning log with 20 ANT warnings. 2 of them are duplicate, all are of priority Normal.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-2316">Issue 2316</a>
     */
    @Test
    public void issue2316() {
        Issues<Issue> warnings = parse("issue2316.txt");

        assertThat(warnings)
                .hasSize(18)
                .hasDuplicatesSize(2)
                .hasHighPrioritySize(0)
                .hasNormalPrioritySize(18)
                .hasLowPrioritySize(0);
    }

    /**
     * Parses a warning log with 3 ANT warnings. They all use different tasks.
     */
    @Test
    public void parseDifferentTaskNames() {
        Issues<Issue> warnings = parse("taskname.txt");

        assertThat(warnings).hasSize(1).hasDuplicatesSize(2);
    }

    /**
     * Verifies that arrays in deprecated methods are correctly handled.
     */
    @Test
    public void parseArrayInDeprecatedMethod() {
        Issues<Issue> warnings = parse("issue5868.txt");

        assertThat(warnings).hasSize(1);

        assertSoftly(softly -> {
            softly.assertThat(warnings.get(0)).hasPriority(Priority.NORMAL)
                    .hasCategory("Deprecation")
                    .hasLineStart(225)
                    .hasLineEnd(225)
                    .hasMessage("loadAvailable(java.lang.String,int,int,java.lang.String[]) in my.OtherClass has been deprecated")
                    .hasFileName("D:/path/to/my/Class.java");
        });
    }

    /**
     * Parses a warning log with 1 warnings that are generated on Japanese environment.
     *
     * @see <a href="http://fisheye.jenkins-ci.org/changelog/Hudson?cs=16376">Commit log on changeset 16376</a>
     * @throws UnsupportedEncodingException if the encoding is not found
     */
    @Test
    public void parseJapaneseWarnings() throws UnsupportedEncodingException {
        // force to use windows-31j - the default encoding on Windows Japanese.
        InputStreamReader is = new InputStreamReader(
                getClass().getResourceAsStream("ant-javac-japanese.txt"), "windows-31j");
        Issues<Issue> warnings = createParser().parse(is, new IssueBuilder());

        assertThat(warnings).hasSize(1);
    }

    @Override
    protected void assertThatIssuesArePresent(final Issues<Issue> issues, final SoftAssertions softly) {
        softly.assertThat(issues).hasSize(1);
        softly.assertThat(issues.get(0))
                .hasPriority(Priority.NORMAL)
                .hasCategory("Deprecation")
                .hasLineStart(28)
                .hasLineEnd(28)
                .hasMessage("begrussen() in ths.types.IGruss has been deprecated")
               .hasFileName("C:/Users/tiliven/.hudson/jobs/Hello THS Trunk - compile/workspace/HelloTHSTest/src/ths/Hallo.java");
    }

    @Override
    protected AbstractParser createParser() {
        return new AntJavacParser();
    }
}

