package cn.ljj.connect.pkg;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.ljj.connect.utils.Logger;

public class PackUtils {
    public static final String TAG = "PackUtils";
    public static final int PACKET_LEN_POSITION = 0;
    public static final int PACKET_URI_POSITION = 4;
    public static final int PACKET_RES_POSITION = 8;
    public static final int HEAD_LEN = 10;
    public static final int STRING_LEN_MAX_LIMIT = 256;

    public static ByteBuffer protoToByteBuffer(AbsPackage msg) {
        int size = msg.size();
        ByteBuffer bb = ByteBuffer.allocate(size + HEAD_LEN);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(size + HEAD_LEN);
        bb.putInt(msg.uri);
        bb.putShort((short) 200);
        bb = msg.packToBuffer(bb);
        bb.flip();
        return bb;
    }

    public static void skipHeader(ByteBuffer bb) {
        bb.position(HEAD_LEN);
    }

    public static short peekRes(ByteBuffer bb) {
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort(PACKET_RES_POSITION);
    }

    public static int peekUri(ByteBuffer message) {
        message.order(ByteOrder.LITTLE_ENDIAN);
        return message.getInt(PACKET_URI_POSITION);
    }

    public static int peekLength(ByteBuffer message) {
        message.order(ByteOrder.LITTLE_ENDIAN);
        return message.getInt(PACKET_LEN_POSITION);
    }

    public static void pack(ByteBuffer bb, byte[] data) {
        if (data != null) {
            bb.putShort((short) data.length);
            bb.put(data);
        } else {
            bb.putShort((short) 0);
        }
    }

    public static void pack(ByteBuffer bb, String string) {
        if (string != null && string.length() > 0) {
            byte[] data = string.getBytes();
            bb.putShort((short) data.length);
            bb.put(data);
        } else {
            bb.putShort((short) 0);
        }
    }

    public static byte[] unPackByteArray(ByteBuffer bb) throws InvalidProtocolData {
        try {
            short byteLen = bb.getShort();
            if (byteLen < 0) {
                throw new InvalidProtocolData("byteLen < 0");
            } else if (byteLen > 0) {
                byte[] e1 = new byte[byteLen];
                bb.get(e1);
                return e1;
            } else {
                return null;
            }
        } catch (BufferUnderflowException var3) {
            throw new InvalidProtocolData(var3);
        }
    }

    public static int calcMarshallSize(byte[] byteArray) {
        return byteArray != null ? 2 + byteArray.length : 2;
    }

    public static int calcMarshallSize(String string) {
        return string != null ? 2 + string.getBytes().length : 2;
    }

    public static <T> ByteBuffer pack(ByteBuffer bb, Collection<T> data, Class<T> elemClass) {
        if (data != null && data.size() != 0) {
            bb.putInt(data.size());
            Iterator<T> iterator = data.iterator();
            while (iterator.hasNext()) {
                Object elem = iterator.next();
                if (elemClass == Integer.class) {
                    bb.putInt(((Integer) elem).intValue());
                } else if (elemClass == Short.class) {
                    bb.putShort(((Short) elem).shortValue());
                } else if (elemClass == Long.class) {
                    bb.putLong(((Long) elem).longValue());
                } else if (elemClass == String.class) {
                    pack(bb, (String) elem);
                } else if (elemClass == byte[].class) {
                    pack(bb, (byte[]) elem);
                } else {
                    if (!(elem instanceof PackageProtocol)) {
                        throw new RuntimeException("unable to marshal element of class " + elemClass.getName());
                    }
                    bb = ((PackageProtocol) elem).packToBuffer(bb);
                }
            }
        } else {
            bb.putInt(0);
        }

        return bb;
    }

    public static <T> void unPack(ByteBuffer bb, Collection<T> data, Class<T> elemClass) throws InvalidProtocolData {
        try {
            int e = bb.getInt();
            for (int i = 0; i < e; ++i) {
                try {
                    Object e1 = null;
                    if (elemClass == Integer.class) {
                        e1 = Integer.valueOf(bb.getInt());
                    } else if (elemClass == Short.class) {
                        e1 = Short.valueOf(bb.getShort());
                    } else if (elemClass == Long.class) {
                        e1 = Long.valueOf(bb.getLong());
                    } else if (elemClass == String.class) {
                        e1 = unPackShortString(bb);
                    } else if (elemClass == byte[].class) {
                        e1 = unPackByteArray(bb);
                    } else {
                        e1 = elemClass.newInstance();
                        if (e1 instanceof PackageProtocol) {
                            ((PackageProtocol) e1).unPackFromBuffer(bb);
                        } else {
                            Logger.e(TAG, "unPack invalid elemClass type " + elemClass.getName());
                        }
                    }
                    data.add((T) e1);
                } catch (Exception var6) {
                    Logger.w(TAG, "unPack failed", var6);
                }
            }
        } catch (BufferUnderflowException var7) {
            throw new InvalidProtocolData(var7);
        }
    }

    public static <T> int calcMarshallSize(Collection<T> data) {
        int pkgSize = 4;
        if (data != null) {
            Iterator<T> iterator = data.iterator();
            while (iterator.hasNext()) {
                Object elem = iterator.next();
                if (elem instanceof Integer) {
                    pkgSize += 4;
                } else if (elem instanceof Short) {
                    pkgSize += 2;
                } else if (elem instanceof Long) {
                    pkgSize += 8;
                } else if (elem instanceof PackageProtocol) {
                    pkgSize += ((PackageProtocol) elem).size();
                } else if (elem instanceof String) {
                    pkgSize += calcMarshallSize((String) elem);
                } else {
                    if (!(elem instanceof byte[])) {
                        throw new IllegalStateException("calcMarshallSize invalid T type:" + elem);
                    }
                    pkgSize += calcMarshallSize((byte[] )elem);
                }
            }
        }
        return pkgSize;
    }

    public static <K, T> ByteBuffer pack(ByteBuffer bb, Map<K, T> data, Class<T> elemClass) {
        if (data != null && data.size() != 0) {
            bb.putInt(data.size());
            Iterator<Entry<K, T>> iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, T> entry = (Map.Entry<K, T>) iterator.next();
                Object key = entry.getKey();
                if (key instanceof Short) {
                    bb.putShort(((Short) key).shortValue());
                } else if (key instanceof Integer) {
                    bb.putInt(((Integer) key).intValue());
                } else if (key instanceof Long) {
                    bb.putLong(((Long) key).longValue());
                } else if (key instanceof String) {
                    pack(bb, (String) key);
                } else {
                    if (!(key instanceof byte[])) {
                        throw new IllegalStateException("pack Map but unknown key type: " + key.getClass().getName());
                    }
                    pack(bb, (byte[]) key);
                }
                Object elem = entry.getValue();
                if (elemClass == Integer.class) {
                    bb.putInt(((Integer) elem).intValue());
                } else if (elemClass == Short.class) {
                    bb.putShort(((Short) elem).shortValue());
                } else if (elemClass == Long.class) {
                    bb.putLong(((Long) elem).longValue());
                } else if (elem instanceof PackageProtocol) {
                    bb = ((PackageProtocol) elem).packToBuffer(bb);
                } else if (elem instanceof String) {
                    pack(bb, (String) elem);
                } else {
                    if (!(elem instanceof byte[])) {
                        throw new IllegalStateException("pack Map but unknown value type: " + elem.getClass().getName());
                    }
                    pack(bb, (byte[]) elem);
                }
            }
        } else {
            bb.putInt(0);
        }
        return bb;
    }

    public static <K, T> void unPack(ByteBuffer bb, Map<K, T> out, Class<K> keyClass, Class<T> elemClass) throws InvalidProtocolData {
        try {
            int e = bb.getInt();

            for (int i = 0; i < e; ++i) {
                Object key = null;
                if (keyClass == Short.class) {
                    key = Short.valueOf(bb.getShort());
                } else if (keyClass == Integer.class) {
                    key = Integer.valueOf(bb.getInt());
                } else if (keyClass == Long.class) {
                    key = Long.valueOf(bb.getLong());
                } else if (keyClass == byte[].class) {
                    key = unPackByteArray(bb);
                } else {
                    if (keyClass != String.class) {
                        throw new IllegalStateException("unPack Map but unknown key type: " + keyClass.getName());
                    }
                    key = unPackShortString(bb);
                }
                try {
                    Object e1 = null;
                    if (elemClass == Integer.class) {
                        e1 = Integer.valueOf(bb.getInt());
                    } else if (elemClass == Short.class) {
                        e1 = Short.valueOf(bb.getShort());
                    } else if (elemClass == Byte.class) {
                        e1 = Byte.valueOf(bb.get());
                    } else if (elemClass == Long.class) {
                        e1 = Long.valueOf(bb.getLong());
                    } else if (elemClass == String.class) {
                        e1 = unPackShortString(bb);
                    } else if (elemClass == byte[].class) {
                        e1 = unPackByteArray(bb);
                    } else if (elemClass == String.class) {
                        e1 = unPackShortString(bb);
                    } else {
                        e1 = elemClass.newInstance();
                        if (!(e1 instanceof PackageProtocol)) {
                            throw new IllegalStateException("unPack Map but unknown value type: " + elemClass.getName());
                        }
                        ((PackageProtocol) e1).unPackFromBuffer(bb);
                    }
                    out.put((K) key, (T) e1);
                } catch (Exception var8) {
                    Logger.w(TAG, "unPack failed", var8);
                }
            }
        } catch (BufferUnderflowException var9) {
            throw new InvalidProtocolData(var9);
        }
    }

    public static <K, T> int calcMarshallSize(Map<K, T> data) {
        int pkgSize = 4;
        if (data != null) {
            Iterator<Entry<K, T>> iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, T> entry = (Map.Entry<K, T>) iterator.next();
                Object key = entry.getKey();
                if (key instanceof Short) {
                    pkgSize += 2;
                } else if (key instanceof Integer) {
                    pkgSize += 4;
                } else if (key instanceof Long) {
                    pkgSize += 8;
                } else if (key instanceof byte[]) {
                    pkgSize += calcMarshallSize((byte[]) key);
                } else {
                    if (!(key instanceof String)) {
                        throw new IllegalStateException("calcMarshallSize Map but unknown key type: " + key.getClass().getName());
                    }
                    pkgSize += calcMarshallSize((String) key);
                }

                Object value = entry.getValue();
                if (value instanceof Integer) {
                    pkgSize += 4;
                } else if (value instanceof Short) {
                    pkgSize += 2;
                } else if (value instanceof Long) {
                    pkgSize += 8;
                } else if (value instanceof PackageProtocol) {
                    pkgSize += ((PackageProtocol) value).size();
                } else if (value instanceof String) {
                    pkgSize += calcMarshallSize((String) value);
                } else {
                    if (!(value instanceof byte[])) {
                        throw new IllegalStateException("calcMarshallSize Map but unknown value type: " + value);
                    }
                    pkgSize += calcMarshallSize((byte[]) value);
                }
            }
        }

        return pkgSize;
    }

    public static String unPackShortString(ByteBuffer bb) throws InvalidProtocolData {
        try {
            short e = bb.getShort();
            if (e < 0) {
                throw new InvalidProtocolData("byteLen < 0");
            } else if (e > 0) {
                byte[] data = new byte[e];
                bb.get(data);
                return new String(data);
            } else {
                return null;
            }
        } catch (BufferUnderflowException var3) {
            throw new InvalidProtocolData(var3);
        }
    }

    public static String limitStringLength(String input, int length) {
        if (input != null && input.length() > 0) {
            return input;
        } else {
            if (input.length() > length) {
                input = input.substring(0, length);
            }
            return input;
        }
    }

    public static <T> void limitStringLengthForMap(Map<T, String> values, int length) {
        if (values != null && values.size() != 0) {
            Iterator<T> iterator = values.keySet().iterator();
            while (iterator.hasNext()) {
                T key = iterator.next();
                String oldValue = values.get(key);
                String newValue = limitStringLength(oldValue, length);
                if ((oldValue != null && newValue != null && !oldValue.equals(newValue))) {
                    values.put(key, newValue);
                }
            }
        }
    }
}
