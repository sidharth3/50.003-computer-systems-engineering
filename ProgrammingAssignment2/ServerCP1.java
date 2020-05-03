package PA2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerCp1 {

    public static void main(String[] args) {

            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            DataOutputStream toClient = null;
            DataInputStream fromClient = null;

            FileOutputStream fileOutputStream = null;
            BufferedOutputStream bufferedFileOutputStream = null;
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader inputReader = null;

            PrintWriter out = null;

            try {
                welcomeSocket = new ServerSocket(4321);

                // Prints IP
                while(true){

                System.out.println("Server's IP: " + welcomeSocket.getInetAddress().getLocalHost().getHostAddress());
                    if(consoleReader.readLine().equals("exit")){
                        break;
                    }
                connectionSocket = welcomeSocket.accept();

                fromClient = new DataInputStream(connectionSocket.getInputStream());
                toClient = new DataOutputStream(connectionSocket.getOutputStream());

                inputReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                out = new PrintWriter(connectionSocket.getOutputStream(), true);

                while (true) {
                    String request = inputReader.readLine();
                    if (request.equals("Can Server Authenticate?")) {
                        System.out.println("Client: " + request);
                        break;
                    } else
                        System.out.println("Request failed");
                }

                ProtocolServer serverProtocol = new ProtocolServer("C:\\Users\\sidha\\IdeaProjects\\ProgrammingAssignment2\\src\\PA2\\example-6b1def70-7ad8-11ea-ae9d-89114163ae84.crt");

                System.out.println("Receiving nonce from client");
                fromClient.read(serverProtocol.getNonce());
                System.out.println("Nonce received, Encrypting now");
                serverProtocol.encryptNonce();

                System.out.println("Replying to Client with Encrypted Nonce");
                toClient.write(serverProtocol.getEncryptedNonce());
                toClient.flush();
                while (true) {
                    String request = inputReader.readLine();
                    if (request.equals("Certificate?")) {
                        System.out.println("Client: " + request);

                        // Send certificate to client
                        System.out.println("Sending Certificate to Client");
                        toClient.write(serverProtocol.getCertificate());
                        toClient.flush();
                        break;
                    } else
                        System.out.println("Request failed.");
                }

                // Waiting for client to finish verification
                System.out.println("Client: " + inputReader.readLine());

                // Starts file transfer
                System.out.println("Identity Verified. File Transfer Starting now!");

                // Get file size from client
                int fileSize = fromClient.readInt();
                System.out.println(fileSize);
                int size = 0;

                int count = 0;
                while (size < fileSize) {

                    int packetType = fromClient.readInt();
                    if (packetType == 0) {

                        System.out.println("Receiving file information");

                        int numBytes = fromClient.readInt();
                        byte[] filename = new byte[numBytes];
                        fromClient.read(filename);
                        fileOutputStream = new FileOutputStream("C:\\Users\\sidha\\IdeaProjects\\ProgrammingAssignment2\\src\\PA2\\recv\\" + new String(filename, 0, numBytes).replace("C:\\Users\\sidha\\IdeaProjects\\ProgrammingAssignment2\\src\\PA2\\", ""));
                        bufferedFileOutputStream = new BufferedOutputStream(fileOutputStream);

                        // If the packet is for transferring a chunk of the file
                    } else if (packetType == 1) {
                        count++;
                        int numBytes = fromClient.readInt();
                        int decryptedNumBytes = fromClient.readInt();
                        size += decryptedNumBytes;

                        byte[] block = new byte[numBytes];
                        fromClient.read(block);

                        // Decrypt each 128 bytes
                        byte[] decryptedBlock = serverProtocol.decryptFile(block);

                        if (numBytes > 0) {
                            bufferedFileOutputStream.write(decryptedBlock, 0, decryptedNumBytes);
                            bufferedFileOutputStream.flush();
                        }
                    }
                }

                // Indicate end of transfer to client
                System.out.println("Transfer complete");
                out.println("End now");

                // Close connection
                System.out.println("Terminating Connection");
                bufferedFileOutputStream.close();
                fileOutputStream.close();

                fromClient.close();
                toClient.close();
                connectionSocket.close();
                Thread.sleep(5000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

}