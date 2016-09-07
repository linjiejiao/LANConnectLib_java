package cn.ljj.connect.ip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender {
	public static final String TAG = "UDPSender";

	private DatagramSocket mDatagramSocket = null;
	private UDPListener mRecvListener = null;

	public UDPSender() throws Exception {
		mDatagramSocket = new DatagramSocket();
	}

	public UDPSender(int localPort, IReadIPListener listener) throws Exception {
		mRecvListener = new UDPListener();
		mDatagramSocket = mRecvListener.startListen(localPort, listener);
	}

	public boolean sendUDP(String ipAddress, int destPort, byte[] data) {
		try {
			InetAddress address = InetAddress.getByName(ipAddress);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, destPort);
			mDatagramSocket.send(sendPacket);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void close() {
		if (mRecvListener != null) {
			mRecvListener.stopListen();
		} else {
			if (mDatagramSocket != null) {
				mDatagramSocket.close();
			}
		}
	}
}
