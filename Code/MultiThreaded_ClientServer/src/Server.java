import java.io.*;
import java.text.*;
import java.util.concurrent.locks.ReentrantLock;
import java.net.*;

// Server class 
public class Server {
	public static void main(String[] args) throws IOException {
		// Server is listening on port 5056
		ServerSocket ss = new ServerSocket(5056);
		// Infinite loop to keep getting requests from clients.
		while (true) {
			Socket s = null;
			try {
				// Reference:
				// https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
				// socket object to receive incoming client requests
				s = ss.accept();

				System.out.println("A new client is connected : " + s);

				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				System.out.println("Assigning new thread for this client");

				// create a new thread object
				Thread t = new ClientHandler(s, dis, dos);

				// Invoking the start() method
				t.start();

			} catch (Exception e) {
				s.close();
				e.printStackTrace();
			}
		}
	}
}

// ClientHandler class 
class ClientHandler extends Thread {
	DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
	DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	ReentrantLock lock = new ReentrantLock();
	int cnt = 0;

	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
	}

	public String delete(String s) {
		String flag = null;
		lock.lock();
		try {
			File f = new File(s);
			if (f.delete()) {
				flag = "File deleted successfully";
				System.out.println("File deleted successfully");
			} else {
				flag = "Failed to delete the file";
				System.out.println("Failed to delete the file");
			}
			cnt++;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			cnt--;
		}
		return flag;
	}

	public String rename(String s1, String s2) {
		String flag = null;
		lock.lock();
		try {
			File f1 = new File(s1);
			File f2 = new File(s2);
			if (f1.renameTo(f2)) {
				flag = "File renamed successfully";
				System.out.println("File renamed successfully");
			} else {
				flag = "Failed to rename the file";
				System.out.println("Failed to rename the file");
			}
			cnt++;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			cnt--;
		}
		return flag;

	}

	public String upload(String s1, String s2) {
		String flag = null;
		lock.lock();
		try {
			final String str = s1;
			final String str1 = s2;
			final File file = new File(str); // path on my system.
			System.out.println("Receving file: " + str);
			System.out.println("Saving as file: " + str);
			final long sz = Long.parseLong(dis.readUTF());
			System.out.println("File Size: " + (sz / (1024 * 1024)) + " MB");
			final byte b[] = new byte[1024];
			System.out.println("Receiving file..");
			final FileOutputStream fos = new FileOutputStream(new File(str1), true);
			long bytesRead;
			do {
				bytesRead = dis.read(b, 0, b.length);
				fos.write(b, 0, b.length);
			} while (!(bytesRead < 1024));
			System.out.println("Completed");
			flag = "File upload complete.";
			fos.close();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			cnt--;
		}
		return flag;
	}

	public String download(String s1, String s2) {
		String flag = null;
		lock.lock();
		try {
			String str = s1;
			String str1 = s2;
			File f = new File(str);
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
			System.out.println("..ok");
			System.out.println("Complete");
			flag = "Download Complete.";
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			cnt--;
		}
		return flag;
	}

	// Reference:
	// https://stackoverflow.com/questions/21686036/calculating-pi-java-program
	public double calculate_pi() {
		double PI = 0;
		//String flag = null;
		for (int i = 10000; i > 0; i--) {
			PI += Math.pow(-1, i + 1) / (2 * i - 1); // Calculate series in parenthesis
			if (i == 1) { // When at last number in series, multiply by 4
				PI *= 4;
			}
		}
		return PI;
	}
	
	public String add(String s1, String s2) {
		String flag = null;
		int a1= Integer.parseInt(s1);
		int a2 = Integer.parseInt(s2);
		int sum = a1+a2;
		flag = Integer.toString(sum);
		return flag;

	}

	@Override
	public void run() {
		String received;
		String toreturn;
		while (true) {
			try {

				// Takes user option input.
				dos.writeUTF("\n\nWhat Operation you want to perform?\n [Upload | Download | Rename | Delete]\nOR\n"
						+ "RPC Calls?\nCalculate_PI:RPC1\nSum:RPC2\nSort:RPC3\nMatrix_Multiplication:RPC4\nOR\n "
						+ "Type Exit to terminate connection.");

				// Receive response from client.
				received = dis.readUTF();
				String pat;
				String pat1;

				if (received.equals("Exit")) {
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
					break;
				}

				// Based on response from the client.
				switch (received) {

				case "Delete":
					pat = dis.readUTF();
					toreturn = delete(pat);
					dos.writeUTF(toreturn);
					break;

				case "Rename":
					pat = dis.readUTF();
					pat1 = dis.readUTF();
					toreturn = rename(pat, pat1);
					dos.writeUTF(toreturn);
					break;

				case "Upload":
					pat = dis.readUTF();
					pat1 = dis.readUTF();
					toreturn = upload(pat, pat1);
					dos.writeUTF(toreturn);
					break;

				case "Download":
					pat = dis.readUTF();
					pat1 = dis.readUTF();
					toreturn = download(pat, pat1);
					dos.writeUTF(toreturn);
					break;
					
				case "RPC1":
					double ans = calculate_pi();
					dos.writeDouble(ans);
					break;
					
				case "RPC2":
					pat = dis.readUTF();
					pat1 = dis.readUTF();
					toreturn = add(pat, pat1);
					dos.writeUTF(toreturn);
					break;
					
				case "RPC3":
					int n = dis.readInt();
					int temp, last = 0;
					int[] a = new int[n];
					for(int i = 0; i < n ; i++)
					{
						a[i]=dis.readInt();
					}
					for (int i = 0; i < n; i++) 
			        {
			            for (int j = i + 1; j < n; j++) 
			            {
			                if (a[i] > a[j]) 
			                {
			                    temp = a[i];
			                    a[i] = a[j];
			                    a[j] = temp;
			                }
			            }
			        }
					for (int i = 0; i < n; i++) 
			        {
			            int at = a[i];
			            dos.writeInt(at);
			        }
					break;
					
				case "RPC4":
					int n1 = dis.readInt();
					int temp1, last1 = 0;
					int[][] a1 = new int[n1][n1];
					int[][] b1 = new int[n1][n1];
					int[][] c1 = new int[n1][n1];
					
					for (int i = 0; i < n1; i++)
			        {
			            for (int j = 0; j < n1; j++)
			            {
			                a1[i][j] = dis.readInt();
			            }
			        }
					for (int i = 0; i < n1; i++)
			        {
			            for (int j = 0; j < n1; j++)
			            {
			                b1[i][j] = dis.readInt();
			            }
			        }
					
					for (int i = 0; i < n1; i++)
			        {
			            for (int j = 0; j < n1; j++)
			            {
			                for (int k = 0; k < n1; k++)
			                {
			                    c1[i][j] = c1[i][j] + a1[i][k] * b1[k][j];
			                }
			            }
			        }
					
					for (int i = 0; i < n1; i++)
			        {
			            for (int j = 0; j < n1; j++)
			            {
			                System.out.print(c1[i][j] + " ");
			                int at = c1[i][j];
				            dos.writeInt(at);
			            }
			        }
					break;
					
				

				default:
					dos.writeUTF("Invalid input");
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			// Closing all the resources.
			this.dis.close();
			this.dos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}