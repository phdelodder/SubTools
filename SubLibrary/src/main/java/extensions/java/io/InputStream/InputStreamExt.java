package extensions.java.io.InputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class InputStreamExt {

    public static String asString(@This InputStream inputStream, Charset charset) throws IOException {
        return new String(inputStream.readAllBytes(), charset);
    }
}
