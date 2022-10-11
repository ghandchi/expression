package ir.ghandchi.interview;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Date & Time: 2022-10-10 15:09
 *
 * @author <a href="mailto:askar.ghandchi@gmail.com">Askar Ghandchi</a>
 * @version 1.0
 */
public class Main {
    private static final int FROM_OPERAND = 1;
    private static final int TO_OPERAND = 100;
    private static final Random random = new Random();

    public static void main(String... args) {

        IntStream.range(1, 27).forEach(level -> {

            IntStream values = random.ints(level, FROM_OPERAND, TO_OPERAND + 1);

            Expression expression = Expression.getBuilder()
                    .level(level)
                    .withValues(values)
                    .build();

            System.out.printf("%2d.  %s = %d\n", level, expression.getExpressionWithValues(), expression.evaluate());
        });
    }
}
