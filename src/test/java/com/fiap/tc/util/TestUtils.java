package com.fiap.tc.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;

public class TestUtils {

    private TestUtils() {
    }

    public static String readResourceFileAsString(String relativeFilePath) {
        try {
            URL resourceUrl = TestUtils.class.getClassLoader().getResource(relativeFilePath);
            if (resourceUrl == null) {
                throw new IllegalArgumentException(format("File not found: %s", relativeFilePath));
            }

            return Files.readString(Paths.get(resourceUrl.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error reading file: " + relativeFilePath, e);
        }
    }
}
