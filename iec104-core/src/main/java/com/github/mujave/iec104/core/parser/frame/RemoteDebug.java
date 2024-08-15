package com.github.mujave.iec104.core.parser.frame;

/**
 * 遥调
 */
public class RemoteDebug implements Frame {


    private boolean se;

    /**
     * 1 降一档 0 升一档
     */
    private int command;

    public boolean isSe() {
        return se;
    }

    public int getCommand() {
        return command;
    }

    public void setSe(boolean se) {
        this.se = se;
    }

    public RemoteDebug(byte value) {
        this.se = (value & 0x80) == 128;
        this.command = value & 0xff & 0x03;
    }

    public RemoteDebug(boolean se, int command) {
        this.se = se;
        this.command = command;
    }

    public String toHex() {
        return se ? String.format("%02X", 128 + command) : String.format("%02X", command);
    }

    @Override
    public String console() {
        return command == 1 ? "降一档" : "升一档";
    }

    @Override
    public String toString() {
        return console();
    }
}
