


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;


public class ProtocolServer {
    private static byte[] nonce = new byte[32];
    private static byte[] encryptedNonce = new byte[128];
    private static byte[] certificate;
    private static InputStream server;
    private static CertificateFactory cf = null;
    private static KeyFactory kf = null;
    private static X509Certificate ServerCert;
    private static PublicKey publicServerKey;
    private static PrivateKey privateKey;
    private static Cipher cipher;
    private static Cipher fdcipher;
  //  private String path = "C:\\Users\\sidha\\IdeaProjects\\ProgrammingAssignment2\\src\\PA2\\private_key.der";
    private String path = "private_key.der";

    public ProtocolServer(String server) throws IOException {
        this.server = new FileInputStream(server);
        try{

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate CAcert =(X509Certificate)cf.generateCertificate(this.server);
            certificate = CAcert.getEncoded();
            publicServerKey = CAcert.getPublicKey();
            privateKey = getPrivateKey(path);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.server.close();
    }

    public static PrivateKey getPrivateKey(String filename) throws Exception{
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }
    public void encryptNonce() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,privateKey);
        encryptedNonce = cipher.doFinal(nonce);
    }
    public byte[] getNonce(){return nonce;}

    public byte[] getEncryptedNonce(){return encryptedNonce;}

    public byte[] getCertificate() {
        return certificate;
    }

    // CP-1 decryption using private key
    public byte[] decryptFile(byte[] fileByte) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        fdcipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        fdcipher.init(Cipher.DECRYPT_MODE,privateKey);
        return fdcipher.doFinal(fileByte);
    }

}


