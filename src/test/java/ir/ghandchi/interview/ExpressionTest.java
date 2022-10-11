package ir.ghandchi.interview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Date & Time: 2022-10-11 10:15
 *
 * @author <a href="mailto:askar.ghandchi@gmail.com">Askar Ghandchi</a>
 * @version 1.0
 */
public class ExpressionTest {

    @Test
    void ensureInvalidLevelPosesAnException() {
        assertThrows(RuntimeException.class,
                () -> Expression.getBuilder()
                        .level(0) // valid range: 1 <= level <= 26
                        .withValues(new int[]{1})
                        .build());
    }

    @Test
    void ensureEmptyValuesPosesAnException() {
        assertThrows(RuntimeException.class,
                () -> Expression.getBuilder()
                        .level(3)
                        .withValues(new int[]{}) // can not be empty
                        .build());
    }

    @Test
    void ensureInconsistentLevelAndValuesPosesAnException() {
        assertThrows(RuntimeException.class,
                () -> Expression.getBuilder()
                        .level(3)
                        .withValues(new int[]{2, 8}) // the number of values should be the same as level
                        .build());
    }

    @Test
    void testEvaluate() {
        Expression expression1 = Expression.getBuilder()
                .expression("7 - 3 * 2 + 19 - 20 / ( 2 + 3)")
                .build();

        Expression expression2 = Expression.getBuilder()
                .expression("7 - (3 * 2) + 19 - (20 / ( 2 + 3))")
                .build();

        assertAll("Both should yield the same.",
                () -> assertEquals(16, expression1.evaluate(), "The result should be 16."),
                () -> assertEquals(16, expression2.evaluate(), "The result should be 16."));
    }

}
