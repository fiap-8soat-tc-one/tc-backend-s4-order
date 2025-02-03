package com.fiap.tc.util;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtils {

    private TestUtils() {
    }

    public static String readResourceFileAsString(Class<? extends Object> testClass, String relativeFilePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(testClass.getResource(relativeFilePath).toURI())), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
