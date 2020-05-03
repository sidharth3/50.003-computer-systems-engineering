import java.lang.Object;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;
import java.nio.*;
import javax.crypto.*;
import java.util.Base64;


public class DesImageSolution {
    public static void main(String[] args) throws Exception {
        EncryptDecryptImage("SUTD.bmp", "CBC", "topdown");
        EncryptDecryptImage("SUTD.bmp", "CBC", "bottomup");
        //EncryptDecryptImage("SUTD.bmp", "ECB");
        //EncryptDecryptImage("triangle.bmp", "ECB");
        EncryptDecryptImage("triangle.bmp", "CBC", "topdown");
        EncryptDecryptImage("triangle.bmp", "CBC", "bottomup");

    }
    public static void EncryptDecryptImage(String fileName, String mode, String direction) throws Exception {
            // read image file and save pixel value into int[][] imageArray
            BufferedImage img = ImageIO.read(new File("C:\\Users\\sidha\\IdeaProjects\\ESCPset3\\src\\" + fileName));
            int image_width = img.getWidth();
            int image_length = img.getHeight();
            //byte[][] imageArray = new byte[image_width][image_length];
            int[][] imageArray = new int[image_width][image_length];
            for(int idx = 0; idx < image_width; idx++) {
                for(int idy = 0; idy < image_length; idy++) {
                    int color = img.getRGB(idx, idy);
                    imageArray[idx][idy] = color;
                }
            }
            SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();
            //you need to try both ECB and CBC mode, use PKCS5Padding padding method
            Cipher desCipher = Cipher.getInstance("DES/" + mode + "/PKCS5Padding");
            desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // define output BufferedImage, set size and format
            BufferedImage outImage = new BufferedImage(image_width,image_length, BufferedImage.TYPE_3BYTE_BGR);

            for(int idx = 0; idx < image_width; idx++) {
                // convert each column int[] into a byte[] (each_width_pixel)
                byte[] each_width_pixel = new byte[4*image_length];
                if(direction.equalsIgnoreCase("topdown")){
                    for(int idy = 0; idy < image_length; idy++) {
                        ByteBuffer dbuf = ByteBuffer.allocate(4);
                        dbuf.putInt(imageArray[idx][idy]);
                        byte[] bytes = dbuf.array();
                        System.arraycopy(bytes, 0, each_width_pixel, idy*4, 4);
                    }
                }
                else {//to do bottom up as in task2 q4
                    for (int idy = image_length - 1; idy > 0; idy--) {
                        ByteBuffer dbuf = ByteBuffer.allocate(4);
                        dbuf.putInt(imageArray[idx][image_length - idy]);
                        byte[] bytes = dbuf.array();
                        System.arraycopy(bytes, 0, each_width_pixel, idy * 4, 4);
                    }
                }
                byte[] encryptedBytes = desCipher.doFinal(each_width_pixel);
                byte[] pixel = new byte[4];
                for(int j = 0; j<image_length; j++){
                    System.arraycopy(encryptedBytes, 4*j, pixel, 0, 4);
                    ByteBuffer buffer = ByteBuffer.wrap(pixel);
                    int newRGB = buffer.getInt();
                    outImage.setRGB(idx, j, newRGB);
                }
            }
//write outImage into file
            ImageIO.write(outImage, "BMP",new File("C:\\Users\\sidha\\IdeaProjects\\ESCPset3\\src\\"+ "En"+ direction + mode + fileName));
        }
    }

