import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ChatServer extends UnicastRemoteObject implements ChatServerInt {

	public Vector v = new Vector();
	private ArrayList groups = new ArrayList();
	Map<String, ArrayList<String>> groupName_users = new HashMap<String, ArrayList<String>>();

	public ChatServer() throws RemoteException {
	}

	public boolean login(ChatClientInt a) throws RemoteException {
		// System.out.println(a.getName() + " got connected....");
		a.tell("You have Connected successfully.");
		// publish(a.getName()+ " has just connected.");
		v.add(a);
		for (int i = 0; i < v.size(); i++) {
			try {
				ChatClientInt tmp = (ChatClientInt) v.get(i);
				tmp.tellUsers(a.getName());
			} catch (Exception e) {

			}
			try {
				fileCommon(a.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean createGroup(String groupName) throws RemoteException {
		groups.add(groupName);
		for (int i = 0; i < v.size(); i++) {
			try {
				ChatClientInt tmp = (ChatClientInt) v.get(i);
				tmp.tellGroups(groupName);
			} catch (Exception e) {

			}
		}
		try {
			fileWrite(groupName_users);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void updategroupMembers(String userName, String groupName, String lastGroup) throws RemoteException {
		ArrayList<String> groupName1 = new ArrayList<String>();
		for (String name : groupName_users.keySet()) {

			String key = name.toString();
			if (key != groupName) {
				ArrayList<String> removeList = new ArrayList<String>();
				removeList = groupName_users.get(key);
				if (removeList.contains(userName)) {
					removeList.remove(userName);
					publishUserList(removeList);
				}
				groupName_users.put(key, removeList);
			}
		}
		if (groupName_users.containsKey(groupName) == true) {
			groupName1 = groupName_users.get(groupName);
			if (!groupName1.contains(userName)) {
				groupName1.add(userName);
				groupName_users.put(groupName, groupName1);			
			}
		} else {
			groupName1.add(userName);
			groupName_users.put(groupName, groupName1);
		}
		publishUserList(groupName1);
		try {
			fileWrite(groupName_users);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// return true;
	}

	public void publishUserList(ArrayList<String> removeList) throws RemoteException{
		for (int i = 0; i < v.size(); i++) {
			try {
				ChatClientInt tmp = (ChatClientInt) v.get(i);
				String name = tmp.getName();
				if (removeList.contains(name)) {
					tmp.tellUsersList(removeList);}} catch (Exception e) {
					}
			}
	}

	public void publish(String s, String groupName) throws RemoteException {
		System.out.println(s);
		ArrayList<String> sendingList = groupName_users.get(groupName);
		for (int i = 0; i < v.size(); i++) {
			try {
				ChatClientInt tmp = (ChatClientInt) v.get(i);
				String name = tmp.getName();
				if (sendingList.contains(name)) {
					tmp.tell(s);
					try {
						fileMsgsWrite(s, groupName);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {

			}
		}
	}

	public void fileCommon(String name) throws IOException {
		File file = new File("//Users//kaushalkabra//Documents//workspace//ChatServer//Logs//allUsers.txt");
		// Here true is to append the content to file
		FileWriter fw = new FileWriter(file, true);
		// BufferedWriter writer give better performance
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(" " + name);
		// Closing BufferedWriter Stream
		bw.close();
	}
	public void fileCommonNew(Vector v2) throws IOException {
		File file = new File("//Users//kaushalkabra//Documents//workspace//ChatServer//Logs//allUsers.txt");
		// Here true is to append the content to file
		FileWriter fw = new FileWriter(file);
		// BufferedWriter writer give better performance
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(" " + v2);
		// Closing BufferedWriter Stream
		bw.close();
	}

	public void fileWrite(Map<String, ArrayList<String>> groupName_users) throws IOException {
		File f = null;
		FileWriter fw = null;
		for (Map.Entry<String, ArrayList<String>> entry : groupName_users.entrySet()) {
			String groupName = entry.getKey();
			File directory = new File("//Users//kaushalkabra//Documents//workspace//ChatServer//Logs//" + groupName);
			if (!directory.mkdir()) {
				//System.out.println("warning");
			}
			f = new File("//Users//kaushalkabra//Documents//workspace//ChatServer//Logs//" + groupName + "//members.txt");

			ArrayList<String> values = entry.getValue();

			PrintWriter pw = new PrintWriter(new FileOutputStream(f));
			for (String club : values)
				pw.println(club);
			pw.close();
			// System.out.println("Key = " + key);
			// System.out.println("Values = " + values);
		}

	}

	public void fileMsgsWrite(String msgs, String groupName) throws IOException {
		File file = new File("//Users//kaushalkabra//Documents//workspace//ChatServer//Logs//" + groupName + "//msgs.txt");
		// Here true is to append the content to file
		FileWriter fw = new FileWriter(file, true);
		// BufferedWriter writer give better performance
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(msgs + " \n");
		// Closing BufferedWriter Stream
		bw.close();
	}

	public Vector getConnected() throws RemoteException {
		return v;
	}

	public ArrayList sendGroupList() throws RemoteException {
		return groups;
	}

	public Map<String, ArrayList<String>> sendGroupMembers() throws RemoteException {
		return groupName_users;
	}
	
	public void removeUser(String userName) throws RemoteException{
		ArrayList<String> groupName1 = new ArrayList<String>();
		//System.out.println(userName);
		//System.out.println("here");
		//v.removeElement(userName);
		if(v.contains(userName)){
				v.remove(userName);	
			}
		for(int i=0 ;i<v.size();i++)
			System.out.println(v.elementAt(i));
		for (String name : groupName_users.keySet()) {

			String key = name.toString();
				ArrayList<String> removeList = new ArrayList<String>();
				removeList = groupName_users.get(key);
				if (removeList.contains(userName)) {
					removeList.remove(userName);
					publishUserList(removeList);
				}
				groupName_users.put(key, removeList);
		}
		try {
			fileWrite(groupName_users);
			fileCommonNew(v);
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	

}