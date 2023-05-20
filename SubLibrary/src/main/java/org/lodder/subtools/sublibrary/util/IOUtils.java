package org.lodder.subtools.sublibrary.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IOUtils {

    public InputStream toInputStream(String text, Charset charset) {
        return new ByteArrayInputStream(text.getBytes(charset));
    }

    public String toString(InputStream inputStream, Charset charset) throws IOException {
        return new String(inputStream.readAllBytes(), charset);
    }
}
