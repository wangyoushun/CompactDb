package com.six.compactdb.dtd;

import java.io.InputStream;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class DTDResolver implements EntityResolver {
    private static final DTDResolver resolver = new DTDResolver();
    private static final String URL = "http://www.iwantfly.sh.cn/";

    public DTDResolver() {
    }

    public static DTDResolver getInstance() {
        return resolver;
    }

    public InputSource resolveEntity(String publicId, String systemId) {
        if(systemId != null && systemId.startsWith(URL)) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream dtdStream = classLoader.getResourceAsStream("com/six/compactdb/dtd/" + systemId.substring(URL.length()));
            if(dtdStream == null) {
                return null;
            } else {
                InputSource source = new InputSource(dtdStream);
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                return source;
            }
        } else {
            return null;
        }
    }
}
