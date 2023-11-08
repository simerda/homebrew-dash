package cz.jansimerda.homebrewdash.helpers;

import java.security.SecureRandom;

public class TokenHelper {
    /**
     * Generate a secure random token of provided length
     * from the following charset: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~+/
     *
     * @param length token length
     * @return generated token
     */
    public static String generateToken(int length) {
        final String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~+/";
        SecureRandom randomProvider = new SecureRandom();

        return randomProvider.ints(length, 0, charset.length())
                .mapToObj(charset::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
