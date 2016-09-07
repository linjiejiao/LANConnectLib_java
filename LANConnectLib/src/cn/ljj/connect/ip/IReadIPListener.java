package cn.ljj.connect.ip;

public interface IReadIPListener {
	public static final int RECEIVE_BUF_SIZE = 10240;

	public static final int END_REASON_INPUT_CLOSED = 0;
	public static final int END_REASON_MANUAL_CLOSED = 1;
	public static final int END_REASON_REACH_STREAM_END= 2;

	void onReceiveData(String ipAddress, int port, byte[] data);

	void onListenEnd(int reason);
}
