import java.util.Base64;
import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.util.stream.BaseStream;


public class DigitalSignatureSolution {

    public static void main(String[] args) throws Exception {
        EncDecDigest("shorttext.txt");
        EncDecDigest("longtext.txt");
    }

    private static void EncDecDigest(String fileName) throws Exception{
        String data = "";
        String line;
        BufferedReader bufferedReader = new BufferedReader( new FileReader("C:\\Users\\sidha\\IdeaProjects\\ESCPset3\\src\\" + fileName));
        while((line= bufferedReader.readLine())!=null){
            data = data +"\n" + line;
        }
        System.out.println("Original content: "+ data);
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA"); keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte[] digest = md.digest();
        String out = Base64.getEncoder().encodeToString(digest);
        System.out.println("Digest: " + out);
        System.out.println("Digest length: " + digest.length);
        System.out.println("File length: " + data.length());

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        rsaCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedDigest = rsaCipher.doFinal(digest);
        System.out.println("Encrypted Digest Length: " + encryptedDigest.length);
        String enBase64format = Base64.getEncoder().encodeToString(encryptedDigest);
        System.out.println("Encrypted Digest: " + enBase64format);

        rsaCipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedDigest = rsaCipher.doFinal(encryptedDigest);
        String deBase64format = Base64.getEncoder().encodeToString(decryptedDigest);
        System.out.println("Decrypted Digest: " + deBase64format);
    }

}