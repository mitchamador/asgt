package gbas.gtbch.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class GzippedResponseEntity {

    private GzippedResponseEntity() {
    }

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

    /**
     * check HttpServletRequest for HttpHeaders.ACCEPT_ENCODING header
     * @param httpRequest
     * @return
     */
    private static boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
        String acceptEncoding = httpRequest.getHeader(HttpHeaders.ACCEPT_ENCODING);
        return acceptEncoding != null && acceptEncoding.contains("gzip");
    }

    /**
     * gzip ResponseEntity
     * @param request
     * @param body
     * @return
     */
    public static ResponseEntity getGzippedResponseEntity(HttpServletRequest request, String body) {

        if (body != null && acceptsGZipEncoding(request)) {

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_ENCODING, "gzip");

            try {
                return new ResponseEntity<>(compress(body.getBytes(StandardCharsets.UTF_8)), headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
