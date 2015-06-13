package com.yume.week104.myappgame20482.twozerogame.Status;

/**
 * Created by Administrator on 2015/6/12.
 */
public class StartStatus extends StatusBase {
    public StartStatus(int indexX, int indexY){
        mIndexX = indexX;
        mIndexY = indexY;
        this.mStatus = STATUS_START;
    }
}
