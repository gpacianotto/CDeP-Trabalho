package Client;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;
public class TCPClient {

	public static BufferedImage convertImage(byte[] data) {
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
	public static void main (String args[]) {
		Socket s = null;
		try{
			int serverPort = 7896;
			s = new Socket("localhost", serverPort);

			String service = args[0];
			String process = args[1];

			DataInputStream in = new DataInputStream( s.getInputStream());
			DataOutputStream out =new DataOutputStream( s.getOutputStream());
			

			if(service.equals("imageProcessor"))
			{

				
				if(process.equals("grayed")) {
					out.writeUTF("grayed");   
					String data = in.readUTF();	    
					System.out.println("Received: "+ data);

					out.writeInt(Integer.parseInt(args[3]));
					String response = in.readUTF();

					System.out.println("Response: "+ response);
					System.out.println("lendo arquivo...");
					byte[] imageData = Files.readAllBytes(Paths.get(args[2]));
					System.out.println("arquivo lido!");
					
					OutputStream outputStream = s.getOutputStream();
					DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

					System.out.println("enviando mensagem ao Servidor TCP");
					dataOutputStream.writeInt(imageData.length);

					
					dataOutputStream.write(imageData, 0, imageData.length);
					dataOutputStream.flush();
					System.out.println("imagem do servidor TCP chegou!");
					System.out.println("desempacotando imagem...");
					int outputLength = in.readInt();
					byte[] outputData = new byte[outputLength];
					in.readFully(outputData, 0, outputLength);
					BufferedImage output = convertImage(outputData);
					System.out.println("imagem desempacotada!");

					String savePath = "./output.png";
					System.out.println("output: " + output.getWidth());
					File f = new File(savePath);
					ImageIO.write(output, "png", f);
				}
				
			}
			// AO EXECUTAR:
			//... crypt encrypt "bla bla bla" passphrase
			// ou
			//... crypt decrypt "AIOJQWOIER" passphrase
			if(service.equals("crypt")) {
				if(process.equals("encrypt")) {
					out.writeUTF(process);
					String data = in.readUTF();	    
					System.out.println("Received: "+ data);

					System.out.println("enviando mensagem para ser encriptada...");
					out.writeUTF(args[2]);
					data = in.readUTF();


					if(data.equals("OK1")){
						
						System.out.println("mensagem enviada com sucesso");
						System.out.println("enviando senha para processar a mensagem...");
						out.writeUTF(args[3]);
						data = in.readUTF();
						if(data.equals("OK2")) {
							System.out.println("senha enviada com sucesso!");
							System.out.println("processando...");

							data = in.readUTF();
							System.out.println();
							System.out.println("Mensagem encriptada: "+data);
						}
					}
					



				}
				else if(process.equals("decrypt")) {
					out.writeUTF(process);
					String data = in.readUTF();
					System.out.println("Received: "+ data);

					System.out.println("enviando mensagem para ser decriptada...");
					out.writeUTF(args[2]);
					data = in.readUTF();

					if(data.equals("OK1")) {
						System.out.println("mensagem enviada com sucesso!");
						System.out.println("enviando senha para processar a mensagem...");
						out.writeUTF(args[3]);
						data = in.readUTF();
						if(data.equals("OK2")) {
							System.out.println("senha enviada com sucesso!");
							System.out.println("processando...");

							data = in.readUTF();
							System.out.println();
							System.out.println("Mensagem decriptada: "+data);
						}
					}
				}
				
			}
			// AO EXECUTAR:
			//... music-scale-generator generate B minor
			// ou
			//... music-scale-generator generate F# major
			else if(service.equals("music-scale-generator")) {
				if(process.equals("generate")) {
					out.writeUTF(process);
					String data = in.readUTF();
					System.out.println("Received: "+ data);

					System.out.println("enviando nota primária...");
					out.writeUTF(args[2]);

					data = in.readUTF();

					if(data.equals("OK1")) {
						System.out.println("nota primária enviada com sucesso!");
						System.out.println("enviando modo da escala...");
						out.writeUTF(args[3]);

						data = in.readUTF();
						if(data.equals("OK2")) {
							System.out.println("modo da escala enviado com sucesso!");
							System.out.println("processando...");

							int length = in.readInt();
							String[] receivedArray = new String[length];
							System.out.println("Recebendo resultados...");
							for (int i = 0; i < length; i++) {
								receivedArray[i] = in.readUTF();
							}

							System.out.println("Escala de "+args[2] +" "+args[3]+" Resultado:");

							for(String str : receivedArray) {
								System.out.print("|"+str+"|");
							}
							System.out.println("pronto!");
						}
						
					}
				}
			}
			else {
				System.out.println("service doesn't exist");
			}

			

		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
     }
}