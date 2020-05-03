import java.io.*;
import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.util.Base64;


public class DesSolution {
    public static void main(String[] args) throws Exception {
    EncryptDecrypt("shorttext.txt");
    EncryptDecrypt("longtext.txt");
    }

    public static void EncryptDecrypt(String fileName) throws Exception {
        String data = "";
        String line;
        byte[] encryptedByteArr;
        byte[] decryptedByteArr;
        BufferedReader bufferedReader = new BufferedReader( new FileReader("C:\\Users\\sidha\\IdeaProjects\\ESCPset3\\src\\" + fileName));
        while((line= bufferedReader.readLine())!=null){
            data = data +"\n" + line;
        }
        System.out.println("Original content: "+ data);
        SecretKey desKey = KeyGenerator.getInstance("DES").generateKey();

        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        desCipher.init(Cipher.ENCRYPT_MODE, desKey);
        encryptedByteArr = desCipher.doFinal(data.getBytes());

        System.out.println(encryptedByteArr);
        System.out.println("Length of encrypted array: " + encryptedByteArr.length);
        System.out.println("Length of text file " + fileName + ": " + data.length());

        String base64format = Base64.getEncoder().encodeToString(encryptedByteArr);
        System.out.println("Encrypted message: " + base64format);

        desCipher.init(Cipher.DECRYPT_MODE, desKey);
        decryptedByteArr = desCipher.doFinal(encryptedByteArr);

        System.out.println(decryptedByteArr);
        String decryptedText = new String(decryptedByteArr);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}