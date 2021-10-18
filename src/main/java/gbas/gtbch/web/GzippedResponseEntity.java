package gbas.gtbch.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

@Component
public class GzippedResponseEntity {

    /**
     * compress to gzip
     * @param body
     * @return
     * @throws IOException
     */
    private static byte[] compress(byte[] body) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
            gzipOutputStream.write(body);
        }
        return baos.toByteArray();
    }


    @Value("${server.compression.enabled:false}")
    private boolean configServerCompression;

    private static boolean isCompressionEnabled;

    @PostConstruct
    public void init() {
        isCompressionEnabled = configServerCompression;
    }

    /**
     * check HttpServletRequest for HttpHeaders.ACCEPT_ENCODING header
     * @param httpRequest
     * @return
     */
    private static boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
        if (isCompressionEnabled) {
            return false;
        } else {
            String acceptEncoding = httpRequest.getHeader(HttpHeaders.ACCEPT_ENCODING);
            return acceptEncoding != null && acceptEncoding.contains("gzip");
        }
    }

    /**
     * gzip ResponseEntity
     * @param request
     * @param body
     * @return
     */
    public static ResponseEntity getGzippedResponseEntity(HttpServletRequest request, String body) {

        HttpHeaders headers = new HttpHeaders();

        if (body != null) {
            if (body.startsWith("[") && body.endsWith("]")) {
                headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            }

            if (acceptsGZipEncoding(request)) {
                headers.add(HttpHeaders.CONTENT_ENCODING, "gzip");

                try {
                    return new ResponseEntity<>(compress(body.getBytes(StandardCharsets.UTF_8)), headers, HttpStatus.OK);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                headers.remove(HttpHeaders.CONTENT_ENCODING);
            }
        }

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

}
