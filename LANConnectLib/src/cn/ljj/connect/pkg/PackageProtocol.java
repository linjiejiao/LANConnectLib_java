package cn.ljj.connect.pkg;

import java.nio.ByteBuffer;

public interface PackageProtocol {
	int size();

	ByteBuffer packToBuffer(ByteBuffer buffer);

	void unPackFromBuffer(ByteBuffer buffer) throws InvalidProtocolData;

}
