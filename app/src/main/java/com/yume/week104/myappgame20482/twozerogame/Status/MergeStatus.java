package com.yume.week104.myappgame20482.twozerogame.Status;

/**
 * Created by Administrator on 2015/6/12.
 */
public class MergeStatus extends StatusBase {
    public MergeStatus(int indexX, int indexY){
        mIndexX = indexX;
        mIndexY = indexY;
        this.mStatus = STATUS_MERGE;
    }
}
