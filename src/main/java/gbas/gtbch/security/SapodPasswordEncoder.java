package gbas.gtbch.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;


public class SapodPasswordEncoder implements PasswordEncoder {
    public static class UUEncoder {

        private UUEncoder() {
        }

        protected static void encodeAtom(OutputStream outputstream, byte[] abyte0, int i, int j)
                throws IOException {
            byte byte1 = 1;
            byte byte2 = 1;
            byte byte0 = abyte0[i];
            if (j > 1)
                byte1 = abyte0[i + 1];
            if (j > 2)
                byte2 = abyte0[i + 2];
            int k = byte0 >>> 2 & 0x3f;
            int l = byte0 << 4 & 0x30 | byte1 >>> 4 & 0xf;
            int i1 = byte1 << 2 & 0x3c | byte2 >>> 6 & 3;
            int j1 = byte2 & 0x3f;
            outputstream.write(k + 32);
            outputstream.write(l + 32);
            outputstream.write(i1 + 32);
            outputstream.write(j1 + 32);
        }

        public static void encode(OutputStream out, byte[] b, int start, int length)
                throws IOException {
            int end = start + length;
            int j;
            for (int i = start; i < end; i = j) {
                j = i + 3;
                if (j <= end)
                    encodeAtom(out, b, i, 3);
                else
                    encodeAtom(out, b, i, end - j);
            }

        }

        public static String encode(byte[] b, int start, int length) {
            try {
                ByteArrayOutputStream baos;
                baos = new ByteArrayOutputStream((length * 4) / 3 + 3);
                int end = start + length;
                int j;
                for (int i = start; i < end; i = j) {
                    j = i + 3;
                    if (j <= end)
                        encodeAtom(baos, b, i, 3);
                    else
                        encodeAtom(baos, b, i, end - j);
                }

                baos.close();
                return baos.toString();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
            return null;
        }
    }

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
            for (byte b : rawPassword.toString().getBytes()) messagedigest.update(b);
            byte[] bEncodedPassword = messagedigest.digest();
            return UUEncoder.encode(bEncodedPassword, 0, bEncodedPassword.length);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}