import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

// Client class 
public class Client {
	public static void main(String[] args) throws IOException {
		try {
			Scanner scn = new Scanner(System.in);
			ReentrantLock lock = new ReentrantLock();
			int cnt = 0;

			// Get localhost IP
			InetAddress ip = InetAddress.getByName("localhost");

			// Establish the connection with server port:5056
			Socket s = new Socket(ip, 5056);

			// Input and Output Stream
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			// Infinite loop to perform actions from client.
			while (true) {
				System.out.println(dis.readUTF());
				String tosend = scn.nextLine();
				dos.writeUTF(tosend);

				if (tosend.equals("Exit")) {
					System.out.println("Closing this connection : " + s);
					s.close();
					System.out.println("Connection closed");
					break;
				}

				if (tosend.equals("Delete")) {
					System.out.println("Enter URL for the file to delete: ");
					String pat = scn.nextLine();
					if (pat.equals("")) {
						System.out.println("Please enter correct path.");
					}
					String c = "Delete";
					dos.writeUTF(pat);
				}

				if (tosend.equals("Rename")) {
					System.out.println("Enter URL for the file to rename: ");
					String pat = scn.nextLine();
					System.out.println("Enter URL for the renamed file to store: ");
					String pat1 = scn.nextLine();
					if (pat.equals("")) {
						System.out.println("Please enter correct path.");
					}
					String c = "Rename";
					dos.writeUTF(pat);
					dos.writeUTF(pat1);
				}

				if (tosend.equals("Upload")) {
					System.out.println("Enter file path to be upload: ");
					String pat = scn.nextLine();
					System.out.println("Enter file path to save: ");
					String pat1 = scn.nextLine();
					File f = new File(pat);
					try {
						dos.writeUTF(pat);
						dos.writeUTF(pat1);
						FileInputStream fin = new FileInputStream(f);
						long sz = (int) f.length();
						byte b[] = new byte[1024];
						int read;
						dos.writeUTF(Long.toString(sz));
						dos.flush();
						System.out.println("Size: " + sz);
						System.out.println("Buf size: " + s.getReceiveBufferSize());
						while ((read = fin.read(b)) != -1) {
							dos.write(b, 0, read);
						}
						fin.close();
						System.out.println("..ok");
						System.out.println("Send Complete");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (tosend.equals("Download")) {
					try {
						System.out.println("Enter file path to be upload: ");
						String pat = scn.nextLine();
						System.out.println("Enter file path to save: ");
						String pat1 = scn.nextLine();
						dos.writeUTF(pat);
						dos.writeUTF(pat1);
						final File file = new File(pat); // path on my system.
						System.out.println("Receving file: " + pat);
						System.out.println("Saving as file: " + pat1);
						final long sz = Long.parseLong(dis.readUTF());
						System.out.println("File Size: " + (sz / (1024 * 1024)) + " MB");
						final byte b[] = new byte[1024];
						System.out.println("Receiving file..");
						final FileOutputStream fos = new FileOutputStream(new File(pat1), true);
						long bytesRead;
						do {
							bytesRead = dis.read(b, 0, b.length);
							fos.write(b, 0, b.length);
						} while (!(bytesRead < 1024));
						System.out.println("Completed");
						fos.close();
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
				
				if(tosend.equals("RPC1"))
				{
					//String received = dis.readUTF();
					double received = dis.readDouble();
					System.out.println(received);
				}
				
				if(tosend.equals("RPC2"))
				{
					System.out.println("Operand 1: ");
					String pat = scn.nextLine();
					System.out.println("Operand 2: ");
					String pat1 = scn.nextLine();
					dos.writeUTF(pat);
					dos.writeUTF(pat1);
				}
				
				if(tosend.equals("RPC3"))
				{

					System.out.println("Number of elements in array: ");
					int n = scn.nextInt();
					dos.writeInt(n);
					int a;
					int[] arr = new int[n];
					for(int i=0;i<n;i++)
					{
						System.out.println("Element " + (i+1) + ": ");
						a = scn.nextInt();
						dos.writeInt(a);
					}
					for (int i = 0; i < n ; i++) 
			        {
						arr[i]=dis.readInt();
			        }
					System.out.println("Sorted array: ");
					for (int i = 0; i < n ; i++) 
			        {
			            System.out.print(arr[i] + ",");
			        }
				}
				
				if(tosend.equals("RPC4"))
				{

					System.out.println("Enter the base of squared matrices: ");
					int n = scn.nextInt();
					dos.writeInt(n);
					int a,b;
					int[][] arr = new int[n][n];
					System.out.println("Enter the elements of 1st martix row wise \n");
					for (int i = 0; i < n; i++)
			        {
			            for (int j = 0; j < n; j++)
			            {
			            	a = scn.nextInt();
							dos.writeInt(a);
			            }
			        }
					
					System.out.println("Enter the elements of 2nd martix row wise \n");
			        for (int i = 0; i < n; i++)
			        {
			            for (int j = 0; j < n; j++)
			            {
			            	b = scn.nextInt();
							dos.writeInt(b);
			            }
			        }
			        
			        for (int i = 0; i < n; i++)
			        {
			            for (int j = 0; j < n; j++)
			            {
			                //System.out.print(c[i][j] + " ");
			                arr[i][j]=dis.readInt();
			            }
			        }
			        
					System.out.println("Answer: \n");
					for (int i = 0; i < n; i++)
			        {
			            for (int j = 0; j < n; j++)
			            {
			                System.out.print(arr[i][j] + " ");
			            }
			            System.out.print("\n");
			        }
				}

				String received = dis.readUTF();
				System.out.println(received);
			}

			// closing resources
			scn.close();
			dis.close();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}