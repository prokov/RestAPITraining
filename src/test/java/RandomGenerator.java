

package test.java;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class RandomGenerator {
    private Random gen = new Random();

    public Integer getRandomInteger(Integer min, Integer max) {
        return gen.nextInt(max - min) + min;

    }

    public Double getRandomDouble(Double min, Double max) {

        return min + gen.nextDouble() * (max - min);

    }

    public float getRandomFloat(float min, float max) {

        return min + gen.nextFloat() * (max - min);

    }

    public Long getRandomLong(Long min, Long max) {
        return getRandomDouble(min.doubleValue(), max.doubleValue()).longValue();

    }

    public String getRandomString(int minLength, int maxLength) {
// String contains only lower cases letter (no digits, no other symbols)

        int len;
        len = getRandomInteger(minLength, maxLength);
        if (maxLength <= minLength)
            return "";

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char tmp = (char) getRandomInteger(97, 123).byteValue(); // 97 code a, 122 code z

            str.append(tmp);
        }
        return str.toString();

    }

    public LocalDate getRandomDate(LocalDate from, LocalDate to) {

        int period = gen.nextInt((int) ChronoUnit.DAYS.between(from, to)); // TO READ ABOUT ChronoUnit
        return from.plusDays(period);
    }

}