import java.rmi.*;
import java.rmi.server.*;
 
public class StartServer {
	public static void main(String[] args) {
		try {
				//System.setSecurityManager(new RMISecurityManager());
			 	java.rmi.registry.LocateRegistry.createRegistry(1099);
			 	
				ChatServerInt b=new ChatServer();	
				Naming.rebind("rmi://172.20.121.159/myabc", b);
				System.out.println("[System] Chat Server is ready.");
			}catch (Exception e) {
					System.out.println("Chat Server failed: " + e);
			}
	}
}