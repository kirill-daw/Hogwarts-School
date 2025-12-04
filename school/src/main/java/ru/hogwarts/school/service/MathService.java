package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.stream.LongStream;

@Service
public class MathService {

    private static final Logger logger = LoggerFactory.getLogger(MathService.class);

    public long calculateSumSlow() {
        logger.info("Calculating sum using slow method");
        long startTime = System.currentTimeMillis();

        long sum = java.util.stream.Stream.iterate(1L, a -> a + 1)
                .limit(1_000_000)
                .reduce(0L, Long::sum);

        long endTime = System.currentTimeMillis();
        logger.debug("Slow method took {} ms, sum: {}", (endTime - startTime), sum);
        return sum;
    }

    public long calculateSumFormula() {
        logger.info("Calculating sum using formula");
        long n = 1_000_000L;
        long sum = n * (n + 1) / 2;
        logger.debug("Formula result: {}", sum);
        return sum;
    }

    public long calculateSumParallel() {
        logger.info("Calculating sum using parallel stream");
        long startTime = System.currentTimeMillis();

        long sum = LongStream.rangeClosed(1, 1_000_000)
                .parallel()
                .sum();

        long endTime = System.currentTimeMillis();
        logger.debug("Parallel method took {} ms, sum: {}", (endTime - startTime), sum);
        return sum;
    }

    public long calculateSumRange() {
        logger.info("Calculating sum using LongStream.rangeClosed");
        long startTime = System.currentTimeMillis();

        long sum = LongStream.rangeClosed(1, 1_000_000)
                .sum();

        long endTime = System.currentTimeMillis();
        logger.debug("Range method took {} ms, sum: {}", (endTime - startTime), sum);
        return sum;
    }
}