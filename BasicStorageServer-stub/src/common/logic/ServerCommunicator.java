package common.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import common.messages.Marshaller;
import common.messages.Message;

public class ServerCommunicator{
	
	private int MAX_MESSAGE_SIZE = 5*128000;
	
	private InputStream input;
	private OutputStream output;
	private Socket socket;
	private Logger logger;
	private Marshaller marshaller;
	
	public ServerCommunicator(Socket socket) throws IOException {
		this.input = socket.getInputStream();
		this.output = socket.getOutputStream();
		this.logger = Logger.getRootLogger();
		this.socket = socket;
		this.marshaller = new Marshaller();
	}

	/**
	 * Method sends a TextMessage using this socket.
	 * @param msg the message that is to be sent.
	 * @throws IOException some I/O error regarding the output stream 
	 */
	public void sendMessage(Message message) throws IOException {
		logger.info("Sending message ...");
		byte[] msgBytes = marshaller.marshal(message);
		output.write(msgBytes, 0, msgBytes.length);
		output.flush();
		logger.info("SEND to \t<" 
				+ socket.getInetAddress().getHostAddress() + ":" 
				+ socket.getPort());
	}
	
	public Message receiveMessage() throws IOException {
		logger.info("Communicator starts receiving message");
		List<Byte> readMessage = new ArrayList<>();
		readMessage.add((byte) input.read());
		while (input.available() > 0 && readMessage.size() <= MAX_MESSAGE_SIZE) {
			readMessage.add((byte) input.read());
		}
		byte[] receivedMessage = convertToByteArray(readMessage);

		Message msg = marshaller.unmarshal(receivedMessage);
		logger.info("RECEIVE from \t<" 
				+ socket.getInetAddress().getHostAddress() + ":" 
				+ socket.getPort());
		return msg;
	}	

	private byte[] convertToByteArray(List<Byte> list) {
		byte[] convertedArray = new byte[list.size()];
		Iterator<Byte> iterator = list.iterator();
		for (int i = 0; i < list.size(); i++) {
			convertedArray[i] = iterator.next();
		}
		return convertedArray;
	}
	
	public void closeConnection() throws IOException {
		if (socket != null) {
			input.close();
			output.close();
			socket.close();
		}
	}
	
}
