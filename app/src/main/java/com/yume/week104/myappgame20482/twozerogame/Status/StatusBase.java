package com.yume.week104.myappgame20482.twozerogame.Status;

/**
 * Created by Administrator on 2015/6/12.
 */
public class StatusBase {
    protected int mIndexX;
    protected int mIndexY;
    protected int mStatus;

    public static final int STATUS_STOP = 0x11;
    public static final int STATUS_MERGE = 0x22;
    public static final int STATUS_DIE = 0x33;
    public static final int STATUS_MOVE = 0x44;
    public static final int STATUS_START = 0x55;

    public int getIndexX() {
        return mIndexX;
    }

    public void setIndexX(int mIndexX) {
        this.mIndexX = mIndexX;
    }

    public int getIndexY() {
        return mIndexY;
    }

    public void setIndexY(int mIndexY) {
        this.mIndexY = mIndexY;
    }

    public int getStatus() {
        return mStatus;
    }
}
