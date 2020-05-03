package PA2;

import PA2.ProtocolClient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientCp1 {

    public static void main(String[] args) {
        //while(true){

        String serverIP = args[0];
        ArrayList<String> filenames = new ArrayList<>();
        for(int i = 1; i<args.length; i++){
            filenames.add("C:\\Users\\sidha\\IdeaProjects\\ProgrammingAssignment2\\src\\PA2\\" + args[i]);
        }

        //String filename = "C:\\\\Users\\\\sidha\\\\IdeaProjects\\\\ProgrammingAssignment2\\\\src\\\\PA2\\" + args[0];
        //String filename = "C:\\Users\\sidha\\IdeaProjects\\ProgrammingAssignment2\\src\\PA2\\100.txt"; //String filename = "audio.mp3";
        //String serverIP = "192.168.56.1";

        int numBytes = 0;

        Socket clientSocket = null;

        DataOutputStream out = null;
        DataInputStream in = null;

        PrintWriter out = null;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedFileInputStream = null;

        BufferedReader in = null;

        long timeStarted = 0;

        try {

            System.out.println("Connecting to the server");

            // Connect to server and get the input and output streams
            clientSocket = new Socket(serverIP, 4321);

            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            ProtocolClient clientProtocol = new ProtocolClient("C:\\Users\\sidha\\IdeaProjects\\ProgrammingAssignment2\\src\\PA2\\cacse.crt");

            out.println("Can Server Authenticate?");
            System.out.println("Can Server Authenticate?");


            System.out.println("Generating nonce and Sending to Server");
            clientProtocol.generateNonce();
            out.write(clientProtocol.getNonce());


            in.read(clientProtocol.getEncryptedNonce());
            System.out.println("Retrieved Encrypted nonce");

            System.out.println("Requesting a certificate from Server");
            out.println("Certificate?");


            clientProtocol.getCertificate(in);
            System.out.println("Attempting to validate Certificate");
            clientProtocol.verifyCert();
            System.out.println("Certificate is validated");


            System.out.println("Verifying Server credentials");
            clientProtocol.getPublicKey();

            byte[] decryptedNonce = clientProtocol.decryptNonce(clientProtocol.getEncryptedNonce());

            if (clientProtocol.validateNonce(decryptedNonce)){
                System.out.println("Server credentials verified");
                out.println("Server verified");
            }else{
                System.out.println("Server verification failed");
                System.out.println("Terminating Connection");
                out.close();
                in.close();
                clientSocket.close();
            }

            System.out.println("Identity Verified. File Transfer starting now!");
// ============================================================================================================
            timeStarted = System.nanoTime();
            int totalSize = 0;
            for(String filename: filenames){
                fileInputStream = new FileInputStream(filename);
                bufferedFileInputStream = new BufferedInputStream(fileInputStream);
                totalSize+= fileInputStream.available(); //total bytes to be sent
            }
            for(String filename: filenames){//iterate through all filenames in the main args
                fileInputStream = new FileInputStream(filename);
                bufferedFileInputStream = new BufferedInputStream(fileInputStream);
                out.writeInt(totalSize);//write total files size to stream
                out.flush();

                out.writeInt(0);
                out.writeInt(filename.getBytes().length);
                out.write(filename.getBytes());
                out.flush();

                byte [] fromFileBuffer = new byte[117];

                int count = 0;

                for (boolean fileEnded = false; !fileEnded;) {


                    numBytes = bufferedFileInputStream.read(fromFileBuffer);//reading in chunks of 117 bytes

                    // Encrypt 117 bytes
                    byte[] encryptedfromFileBuffer = clientProtocol.encryptFile(fromFileBuffer);
                    count++;
                    fileEnded = numBytes < fromFileBuffer.length;
                    int encryptedNumBytes = encryptedfromFileBuffer.length;

                    out.writeInt(1);
                    out.writeInt(encryptedNumBytes);
                    out.writeInt(numBytes);
                    out.write(encryptedfromFileBuffer);
                    out.flush();
                }
                System.out.println(filename);

                ;

            }
            // to denote end of transfer
            while (true){
                String end = in.readLine();
                if (end.equals("End now")){
                    System.out.println("Server: " + end);
                    break;
                }
                else
                    System.out.println("End request failed");
            }
            System.out.println("Terminating Connection");
            bufferedFileInputStream.close();
            fileInputStream.close();

        } catch (Exception e) {e.printStackTrace();}

        long timeTaken = System.nanoTime() - timeStarted;
        double millis = timeTaken/1000000.0;
        System.out.println("Program took: " + millis + "ms to run");
        }
   // }
}