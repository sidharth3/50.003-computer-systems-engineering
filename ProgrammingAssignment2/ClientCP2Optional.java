

import javax.crypto.Cipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
public class ClientCP2Optional {

    static  BufferedReader in = null;
    static  DataOutputStream ServerOutput = null;
    static  FileInputStream fileInputStream = null;
    static  PrintWriter out = null;
    static  Socket clientSocket = null;
    static  DataInputStream ServerInput = null;
    static  String filename;
    static  String serverIP;
    public static void main(String[] args) {

         serverIP = args[0];
        String command ;
        while(true){
        	try{
        		
                System.out.println("Enter upload/download/delete validfilename");
                Scanner scanner = new Scanner(System.in);
                
                command = scanner.nextLine();
                if(command.equals("exit")){
                    init();
                    out.println("exit");
                    System.exit(0);
                }
                
                if(command.split(" ",4).length <2)
                    continue;
                
                    filename = command.split(" " , 4)[1];
                command  = command.split(" " , 4)[0];

                File tmpDir = new File(filename);
                boolean exists = tmpDir.exists();
                
                if (command.equals("delete")||command.equals("download")) exists = true;
        
                if(exists == false ||(!command.equals("upload") && !command.equals("download") && !command.equals("delete") )) continue;
                
                init();
                System.out.println("Process: " + command + " " + filename);

                if(command.equals("upload")){
                    System.out.println("Uploading file to Server now...");
                    out.println("upload");
                    upload();
                }
                else if(command.equals("delete")){
                    System.out.println("Deleting file from Server now...");
                    out.println("delete");
                    delete();
                }
                    else{out.println("download");
                    System.out.println("gotcha");
                    download();
                }
         }
        catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        
    }
}
    public static void upload(){
        if(filename == null){
            Scanner scanner = new Scanner(System.in);
            filename = scanner.nextLine();
        }
        
        long timeStarted = 0;
        try {
            ProtocolClient ProtocolClient = new ProtocolClient("cacse.crt");

            out.println("We are waiting for server to prove its the server indeed");
            System.out.println("We are going to validate the certicate (AP)");

   
            System.out.println("Nonce creation");
            ProtocolClient.generateNonce();
            System.out.println("Sending our nonce to the server which it will encrypt and send back");
            ServerOutput.write(ProtocolClient.getNonce());
            ServerInput.read(ProtocolClient.getEncryptedNonce());
            System.out.println("Gotten the encryptednonce");

         
            System.out.println("Now we ask the server for its certificate (AP)");
            out.println("Checking for a valid certificate");


            ProtocolClient.getCertificate(ServerInput); //Using ServerInput.read(ProtocalClinet.getCertifcate) did not workhmm
            System.out.println("Certificate is being validated");
            ProtocolClient.verifyCert();
            System.out.println("Certificate validated");
            System.out.println("Now to very the server");
            ProtocolClient.getPublicKey();
            byte[] decryptedNonce = ProtocolClient.decryptNonce(ProtocolClient.getEncryptedNonce());
//we basically have to now decryt the encyrpted nonce and then check if the nonce we sent equals the nonce we got. This implies that the server indeed sneds a valid public key and is legit

            if (ProtocolClient.validateNonce(decryptedNonce)){
                
                out.println("SUCCESSFULY VERIFIED");
                System.out.println("THe Server has been successfully verified");
            } else{
                System.out.println("ERROR FAILED");
                System.out.println("tERMINATING ALL THE CONNECTIONS");
                ServerOutput.close();
                ServerInput.close();
                clientSocket.close();
            }

            System.out.println("CP2 BASED TRANSFER");

//Create a Cipher similar to the lab acitivity
            SecretKey seshKey = KeyGenerator.getInstance("AES").generateKey();
            Cipher sessionCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            sessionCipher.init(Cipher.ENCRYPT_MODE, seshKey);

            //encryping sesh key with the public key server sent
            byte[] encryptedseshKey = ProtocolClient.encryptFile(seshKey.getEncoded());
            System.out.println(Base64.getEncoder().encodeToString(encryptedseshKey));

            BufferedOutputStream outputStream = new BufferedOutputStream(ServerOutput);

            timeStarted = System.nanoTime();

         
            ServerOutput.writeInt(1);
            ServerOutput.writeInt(encryptedseshKey.length);
            ServerOutput.flush();

            outputStream.write(encryptedseshKey, 0, encryptedseshKey.length);
            outputStream.flush();

            System.out.println("Encrypted Session key has been sent");
            File file = new File(filename);
            fileInputStream = new FileInputStream(file);
            byte[] fileByteArray = new byte[(int)file.length()];
            fileInputStream.read(fileByteArray, 0, fileByteArray.length);//  storing in filebytearray
            fileInputStream.close();
            ServerOutput.writeInt(0);
            ServerOutput.writeInt(filename.getBytes().length); //send this similar to lab 5 as a byte array
            ServerOutput.flush();

            outputStream.write(filename.getBytes());
            outputStream.flush();
            byte[] encryptedFile = sessionCipher.doFinal(fileByteArray); //encrypting file
            System.out.println(Base64.getEncoder().encodeToString(encryptedFile));
            ServerOutput.writeInt(8);
            System.out.println("the length of our encrypted file: " + encryptedFile.length);
            ServerOutput.writeInt(encryptedFile.length);
            ServerOutput.flush();

           
            ServerOutput.write(encryptedFile, 0, encryptedFile.length);
            ServerOutput.flush();

            while (true) {
                String end = in.readLine();
                if (end.equals("Termination of transferring")){
                    System.out.println("Server: " + end); //communciation ending
                    break;
                }
                else
                    System.out.println("End request failed...");
            }

            System.out.println("Type another file name");
            fileInputStream.close();

        } catch (Exception e) {e.printStackTrace();}

        long timeTaken = System.nanoTime() - timeStarted;
        System.out.println("Program took: " + timeTaken/1000000.0 + "ms to run");
filename =null;
 
    }
    
    public static void init() {
        try {
            clientSocket = new Socket(serverIP, 4321);

            ServerOutput = new DataOutputStream(clientSocket.getOutputStream());
            ServerInput = new DataInputStream(clientSocket.getInputStream());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(){
        System.out.println("going to delete");

        if (filename == null) {
            Scanner scanner = new Scanner(System.in);
            filename = scanner.nextLine();

        }

        long timeStarted = 0;
        try {
            ProtocolClient ProtocolClient = new ProtocolClient("cacse.crt");

            out.println("Requesting server authentication");
            System.out.println("We are going to validate the certicate (AP)");
            ProtocolClient.generateNonce();
            System.out.println("Sending our nonce to the server which it will encrypt and send back");
            ServerOutput.write(ProtocolClient.getNonce());
            ServerInput.read(ProtocolClient.getEncryptedNonce());
            System.out.println("Gotten the encryptednonce");

            System.out.println("Authenticating Server Identity...");
            out.println("certificate check");

            ProtocolClient.getCertificate(ServerInput);
            System.out.println("Certificate is being validated");
            ProtocolClient.verifyCert();
            System.out.println("Certificate validated");
            System.out.println("Now to very the server");
            ProtocolClient.getPublicKey();
            byte[] decryptedNonce = ProtocolClient.decryptNonce(ProtocolClient.getEncryptedNonce());

            if (ProtocolClient.validateNonce(decryptedNonce)) {

                out.println("SUCCESSFULY VERIFIED");
                System.out.println("THe Server has been successfully verified");
            } else {
                System.out.println("ERROR FAILED");
                System.out.println("tERMINATING ALL THE CONNECTIONS");
                ServerOutput.close();
                ServerInput.close();
                clientSocket.close();
            }

            System.out.println("CP2 BASED TRANSFER");

            SecretKey seshKey = KeyGenerator.getInstance("AES").generateKey();
            Cipher sessionCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            sessionCipher.init(Cipher.ENCRYPT_MODE, seshKey);

            byte[] encryptedseshKey = ProtocolClient.encryptFile(seshKey.getEncoded());
            System.out.println(Base64.getEncoder().encodeToString(encryptedseshKey));

            BufferedOutputStream outputStream = new BufferedOutputStream(ServerOutput);
            timeStarted = System.nanoTime();
            ServerOutput.writeInt(1);
            ServerOutput.writeInt(encryptedseshKey.length);
            ServerOutput.flush();

            outputStream.write(encryptedseshKey, 0, encryptedseshKey.length);
            outputStream.flush();

            System.out.println("Encrypted Session key has been sent");
            File file = new File(filename);

            ServerOutput.writeInt(0);
            ServerOutput.writeInt(filename.getBytes().length); 
            ServerOutput.flush();

            outputStream.write(filename.getBytes());
            outputStream.flush();

            while (true) {
                String end = in.readLine();
                if (end.equals("Transfer complete")) {
                    System.out.println("Server: " + end);
                    break;
                } else
                    System.out.println("End request failed...");
            }
            System.out.println("Type another file name");
            ;

        } catch (Exception e) {
            e.printStackTrace();
        }

        long timeTaken = System.nanoTime() - timeStarted;
        System.out.println("Program took: " + timeTaken / 1000000.0 + "ms to run");
        filename = null;

    }

    public static void download(){
    	System.out.println("going to donwload");

    	int counter=0;
        if(filename ==null){
            Scanner scanner = new Scanner(System.in);
            filename = scanner.nextLine();
        }
        long timeStarted = 0;
        try {
            ProtocolClient ProtocolClient = new ProtocolClient("cacse.crt");

            out.println("We are waiting for server to prove its the server indeed");
            System.out.println("We are going to validate the certicate (AP)");

   
            System.out.println("Nonce creation");
            ProtocolClient.generateNonce();
            System.out.println("Sending our nonce to the server which it will encrypt and send back");
            ServerOutput.write(ProtocolClient.getNonce());
            ServerInput.read(ProtocolClient.getEncryptedNonce());
            System.out.println("Gotten the encryptednonce");

         
            System.out.println("Now we ask the server for its certificate (AP)");
            out.println("Checking for a valid certificate");


            ProtocolClient.getCertificate(ServerInput); //Using ServerInput.read(ProtocalClinet.getCertifcate) did not workhmm
            System.out.println("Certificate is being validated");
            ProtocolClient.verifyCert();
            System.out.println("Certificate validated");
            System.out.println("Now to very the server");
            ProtocolClient.getPublicKey();
            byte[] decryptedNonce = ProtocolClient.decryptNonce(ProtocolClient.getEncryptedNonce());

            if (ProtocolClient.validateNonce(decryptedNonce)){
                
                out.println("SUCCESSFULY VERIFIED");
                System.out.println("THe Server has been successfully verified");
            } else{
                System.out.println("ERROR FAILED");
                System.out.println("tERMINATING ALL THE CONNECTIONS");
                ServerOutput.close();
                ServerInput.close();
                clientSocket.close();
            }

            System.out.println("CP2 BASED TRANSFER");

//Create a Cipher similar to the lab acitivity
            SecretKey seshKey = KeyGenerator.getInstance("AES").generateKey();
            Cipher sessionCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            sessionCipher.init(Cipher.ENCRYPT_MODE, seshKey);

            //encryping sesh key with the public key server sent
            byte[] encryptedseshKey = ProtocolClient.encryptFile(seshKey.getEncoded());
            System.out.println(Base64.getEncoder().encodeToString(encryptedseshKey));

            BufferedOutputStream outputStream = new BufferedOutputStream(ServerOutput);
//We could use the PrintWRiter however we using BUfferedReader as we need the output to stay in the buffer to be read as and when

            // begin clocking the file transfer
            timeStarted = System.nanoTime();

         
            ServerOutput.writeInt(1);
            ServerOutput.writeInt(encryptedseshKey.length);
            ServerOutput.flush();

            outputStream.write(encryptedseshKey, 0, encryptedseshKey.length);
            outputStream.flush();

            System.out.println("Encrypted Session key has been sent");
            
            ServerOutput.writeInt(0);
            ServerOutput.writeInt(filename.getBytes().length); //send this similar to lab 5 as a byte array
            ServerOutput.flush();
            
            outputStream.write(filename.getBytes());
            outputStream.flush();
            System.out.println("before the int is read");

            int signal =  ServerInput.readInt();
            System.out.println("signal is "+signal);
            if (signal == 8) {
                   
                    System.out.println("Going to receieve the file ");
            

                    int encryptedFileSize = ServerInput.readInt();
                    System.out.println("the file size is " + encryptedFileSize);

                    byte[] encryptedFileBytes = new byte[encryptedFileSize];
                    ServerInput.readFully(encryptedFileBytes, 0, encryptedFileSize);
                    System.out.println(Arrays.toString(encryptedFileBytes));
                    System.out.println(encryptedFileBytes.length);

                    System.out.println("UH OH it is encrypted. Let us decrypt it coz we got public key");
                    sessionCipher.init(Cipher.DECRYPT_MODE, seshKey);
                    byte[] result = sessionCipher.doFinal(encryptedFileBytes);
                

                    FileOutputStream file = new FileOutputStream("RECIEVED_" + filename); //creating output file
                    file.write(result);
                    file.close();

                    System.out.println("Done!");
                   // out.println("Termination of transferring");

                   
                    System.out.println("Send more!");
                    counter+=1;
                    ServerOutput.close();
                    ServerInput.close();
                    
                    System.out.println("downloaded!");
                }

           

            System.out.println("Type another file name");
            fileInputStream.close();

        }

         catch (Exception e) {System.out.println("Saved as RECIEVED_<filename>");}

        long timeTaken = System.nanoTime() - timeStarted;
        System.out.println("Program took: " + timeTaken/1000000.0 + "ms to run");
filename =null;
 
    }
}
