package Server;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;
import compute.Compute;
public class TCPServer {
	public static void main (String args[]) {
		try{
			int serverPort = 7896;
			ServerSocket listenSocket = new ServerSocket(serverPort);
			while(true) {
				Socket clientSocket = listenSocket.accept();
				Connection c = new Connection(clientSocket);
			}
		} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
	}
}
class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	public Connection (Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream());
			this.start();
		} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
	}
	//aplica o compute de StringCryptographer
	public int[][][] computeGrayed(int[][][] i, int layer) {

		try {
			String name = "Compute";
			Registry registry = LocateRegistry.getRegistry("localhost",1099);
			Compute comp = (Compute) registry.lookup(name);
			ImageManipulator task = new ImageManipulator(i, layer);
			int[][][] output = comp.executeTask(task);
			return output;
		}
		catch (Exception e) {
            System.err.println("ComputeGrayed exception:");
            e.printStackTrace();
        }
		
		return new int[0][0][0];
		
		//INSPIRAÇÃO DA AULA 4
		// Pi task = new Pi(Integer.parseInt(args[2]));
		// BigDecimal pi = comp.executeTask(task);
		// System.out.println(pi);
	}
	//aplica o compute de StringCryptographer
	public String computeCrypt(String message, String passphrase, String action) {
		try {
			String name = "Compute";
			Registry registry = LocateRegistry.getRegistry("localhost",1099);
			Compute comp = (Compute) registry.lookup(name);
			StringCryptographer task = new StringCryptographer(message, passphrase, action);
			String output = comp.executeTask(task);
			return output;
		}
		catch (Exception e) {
            System.err.println("ComputeCrypt exception:");
            e.printStackTrace();
        }

		return "";
	}
	//aplica o compute de MusicNoteGenerator
	public String[] computeMusicNoteGenerator(String key, String mode) {
		try {
			String name = "Compute";
			Registry registry = LocateRegistry.getRegistry("localhost",1099);
			Compute comp = (Compute) registry.lookup(name);
			MusicNoteGenerator task = new MusicNoteGenerator(key, mode);
			String[] output = comp.executeTask(task);
			return output;
		}
		catch (Exception e) {
            System.err.println("ComputeMusicNoteGenerator exception:");
            e.printStackTrace();
        }

		return new String[0];
	}

	public BufferedImage convertImage(byte[] data) {
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
			return image;
		}
		catch(Exception e) {
			System.out.println("erro ao converter imagem: ");
			e.printStackTrace();
		}
		 
		return new BufferedImage(null, null, false, null);
	}

	public byte[] convertToByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] imageData = baos.toByteArray();
            baos.close();
            return imageData;
        } catch (IOException e) {
            System.out.println("Error converting image to byte array:");
            e.printStackTrace();
        }
        return new byte[0];
    }

	public static int[][][] convertTo3DArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int numColorChannels = 3; 

        
        byte[] pixelData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        int[][][] imageData = new int[width][height][numColorChannels];

        int pixelIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int channel = 0; channel < numColorChannels; channel++) {
                    int colorValue = pixelData[pixelIndex] & 0xFF;
                    imageData[x][y][channel] = colorValue;
                    pixelIndex++;
                }
            }
        }
		return imageData;
    }
	
	public static BufferedImage convertToBufferedImage(int[][][] image) {
        int width = image.length;
        int height = image[0].length;
        int colorChannels = image[0][0].length;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = 0;
                for (int c = 0; c < colorChannels; c++) {
                    int colorValue = image[x][y][c];
                    rgb |= (colorValue << (8 * (2 - c)));
                }
                bufferedImage.setRGB(x, y, rgb);
            }
        }

        return bufferedImage;
    }

	public void run(){
		try {			                 

				String data = in.readUTF();	         
				System.out.println("data: "+ data);         
				out.writeUTF("Service will run: "+ data);

				if(data.equals("grayed"))
				{
					InputStream inputStream = clientSocket.getInputStream();
					DataInputStream dataInputStream = new DataInputStream(inputStream);

					int layer = in.readInt();
					System.out.println("layer: "+layer);
					out.writeUTF("OK!");
					System.out.println("recebendo imagem...");
					int imageDataLength = dataInputStream.readInt();
					System.out.println("imagem recebida");
					byte[] imageData = new byte[imageDataLength];
					dataInputStream.readFully(imageData, 0, imageDataLength);

					System.out.println("processando imagem, aguarde alguns instantes....");

					BufferedImage im = convertImage(imageData);

					System.out.println("imagem convertida para BufferedImage");

					int[][][] dataimage = convertTo3DArray(im);

					System.out.println("imagem convertida para int[][][]");
					System.out.println("executando processamento computeGrayed...");

					int[][][] outputdata = computeGrayed(dataimage, layer);

					System.out.println("computeGrayed concluído");
					System.out.println("convertendo para BufferedImage");

					BufferedImage output = convertToBufferedImage(outputdata);

					System.out.println("Conversão concluída");
					System.out.println("convertendo para byte");

					byte[] byteoutput = convertToByteArray(output);

					System.out.println("Conversão concluída");
					System.out.println("enviando resultado para TCPClient");

					out.writeInt(byteoutput.length);
					out.write(byteoutput, 0, byteoutput.length);

					

					System.out.println("Image received: "+ imageDataLength );
				}

				else if(data.equals("encrypt")) {
					out.writeUTF("OK1");
					String messageToEncrypt = in.readUTF();
					System.out.println("received: "+messageToEncrypt);
					out.writeUTF("OK2");
					String passphraseToEncrypt = in.readUTF();
					System.out.println("received: "+passphraseToEncrypt);


					
					System.out.println("encriptando...");
					String encryptedMessage = computeCrypt(messageToEncrypt, passphraseToEncrypt, "encrypt");

					System.out.println("Mensagem encriptada, mandando de volta...");

					out.writeUTF(encryptedMessage);
				}

				else if(data.equals("decrypt")) {
					out.writeUTF("OK1");
					String messageToDecrypt = in.readUTF();
					System.out.println("received: "+messageToDecrypt);
					out.writeUTF("OK2");
					String passphraseToDecrypt = in.readUTF();
					System.out.println("received: "+passphraseToDecrypt);

					System.out.println("decriptando...");
					String decryptedMessage = computeCrypt(messageToDecrypt, passphraseToDecrypt, "decrypt");

					System.out.println("Mensagem decriptada, mandando de volta...");

					out.writeUTF(decryptedMessage);
				}
				else if(data.equals("generate")) {
					out.writeUTF("OK1");

					String keyNote = in.readUTF();
					System.out.println("received: "+keyNote);
					out.writeUTF("OK2");
					String mode = in.readUTF();
					System.out.println("received: "+mode);

					String[] scale = computeMusicNoteGenerator(keyNote, mode);

					System.out.println("Escalas geradas, mandando de volta...");

					out.writeInt(scale.length);

					for(String str : scale) {
						out.writeUTF(str);
					}
					System.out.println("pronto!");
					out.flush();
				}

				else {
					System.out.println("Este serviço não existe");
				}

				
			
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}
		

	}
}