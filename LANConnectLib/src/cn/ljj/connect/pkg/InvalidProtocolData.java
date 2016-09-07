package cn.ljj.connect.pkg;

import java.nio.BufferUnderflowException;

public class InvalidProtocolData extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidProtocolData(BufferUnderflowException e) {
        super("Invalid Protocol Data", e);
    }

    public InvalidProtocolData(String error) {
        super(error);
    }
}
