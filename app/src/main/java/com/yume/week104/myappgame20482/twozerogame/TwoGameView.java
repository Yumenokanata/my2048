package com.yume.week104.myappgame20482.twozerogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

import com.yume.week104.myappgame20482.Constants;
import com.yume.week104.myappgame20482.GameView;
import com.yume.week104.myappgame20482.R;
import com.yume.week104.myappgame20482.twozerogame.Status.DieStatus;
import com.yume.week104.myappgame20482.twozerogame.Status.MergeStatus;
import com.yume.week104.myappgame20482.twozerogame.Status.MoveStatus;
import com.yume.week104.myappgame20482.twozerogame.Status.StatusBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin1 on 15-6-11.
 */
public class TwoGameView extends GameView implements Constants {
    private static final String TAG = "TwoGameView";

    private static final String KEY_INDEX_W = "index_w";
    private static final String KEY_INDEX_H = "index_h";
    private static final String KEY_DATA = "data";

    long mPeriod = 22;
    int MODE;
    long crazy_mode_time = 0;
    long crazy_mode_add_time = 500;

    List<CubeRect> mCubeList;
    float mWidth;
    float mHeight;
    float mPaddingW;
    float mPaddingH;
    float mCubePadding;
    RectF mCanvasRectF = new RectF();
    Paint mCanvasRectPaint;
    RectF mBackRectF = new RectF();
    Paint mBackPaint;

    float startX;
    float startY;

    public static int INDEX_W = 4;
    public static int INDEX_H = 4;

    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_DOWN = 3;

    Random mRandom;
    Timer mTimer;
    SoundPool mSoundPool;

    int SOUND_MERGE;
    int SOUND_MOVE;

    boolean haveChanged = false;
    boolean haveMerge = false;
    boolean haveMove = false;
    boolean addOne = false;
    boolean startTouch = false;

    boolean isInit = false;
    boolean isStart = false;

    public TwoGameView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TwoGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init(){
        mCanvasRectPaint = new Paint();
        mCanvasRectPaint.setColor(0xffBBAE9F);

        mBackPaint = new Paint();
        mBackPaint.setColor(0xffCCC0B2);

        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        SOUND_MERGE = mSoundPool.load(mContext, R.raw.merge, 0);
        SOUND_MOVE = mSoundPool.load(mContext, R.raw.move, 0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = getWidth();
        mHeight = getHeight();

        if(mHeight / mWidth < INDEX_H * 1.0f / INDEX_W){
            mPaddingH = mHeight / 13;
            mPaddingW = (mWidth - (mHeight - mPaddingH * 2) / INDEX_H * INDEX_W) / 2;
        }else{
            mPaddingW = mWidth / 13;
            mPaddingH = (mHeight - (mWidth - mPaddingW * 2) / INDEX_W * INDEX_H) / 2;
        }
        Log.d(TAG, "mPaddingW = " + mPaddingW + "; mPaddingH = " + mPaddingH);
//        mPadding = mWidth > mHeight ? (mWidth - (mHeight - mHeight / 13 * 2) / INDEX_H * INDEX_W) / 2 : mWidth / 13;
        mCubePadding = (mWidth - mPaddingW * 2) / INDEX_W / 13;
        float width = mWidth - 2 * mPaddingW;
        float height = mHeight - 2 * mPaddingH;
        Log.d(TAG, "width = " + width + "; height = " + height);
//        mCanvasRectF.set(mPadding, (mHeight - width) / 2, mPadding + width, mHeight - (mHeight - width) / 2);
        mCanvasRectF.set(mPaddingW, mPaddingH, mPaddingW + width, mPaddingH + height);
        mBackRectF.set(0,
                0,
                mCanvasRectF.width() / INDEX_W,
                mCanvasRectF.height() / INDEX_H);
        mBackRectF.inset(mCubePadding, mCubePadding);

        if(!isInit){
            initializeData();
            Log.d(TAG, "initializeData");
            isInit = true;
        }else{
            for(CubeRect cb : mCubeList)
                cb.reMeasure(mCanvasRectF,
                        mCanvasRectF.width() / INDEX_W,
                        mCanvasRectF.height() / INDEX_H);
        }
    }

    @Override
    public void setSeed(String seed) {
        mRandom = new Random(Long.valueOf(seed));
    }
    // 此方法请在初始化数据前调用
    public void setMode(int MODE) {
        this.MODE = MODE;
    }

    @Override
    public void reset() {
        isStart = false;
        stopUpdateTimer();
        initializeData();
        startUpdateTimer();
    }

    @Override
    public void startUpdateTimer() {
        stopUpdateTimer();
        mTimer = new Timer();
        mTimer.schedule(new UpdateTimerTask(), 0, mPeriod);
    }

    @Override
    public void stopUpdateTimer() {
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    class UpdateTimerTask extends TimerTask{
        @Override
        public void run() {
            if(mCubeList == null)
                return;
//            if(!updateStatus())
//                postInvalidate();
            updateStatus();
            postInvalidate();
        }
    }

    private boolean updateStatus(){
        boolean allOver = true;
        for(int i = 0; i < mCubeList.size(); i++){
            CubeRect cr = mCubeList.get(i);
            cr.updateStatus();
            if(cr.getCurrentStatus().getStatus() == StatusBase.STATUS_DIE){
                mCubeList.remove(cr);
                i--;
                continue;
            }
            if(cr.getCurrentStatus().getStatus() != StatusBase.STATUS_STOP){
                allOver = false;
            }
            if(MODE == MODE_TIME && cr.getNum() == 2048){
                stopUpdateTimer();
                if(mOnGameStatusChangedListener != null){
                    mOnGameStatusChangedListener.completeOnRound(true);
                }
            }
        }

        if(haveMove){
            mSoundPool.play(SOUND_MOVE, 1, 1, 0, 0, 1);
            haveMove = false;
        }
        if(haveMerge){
            mSoundPool.play(SOUND_MERGE, 1, 1, 0, 0, 1);
            haveMerge = false;
        }

        if(allOver){
            if(addOne && mCubeList.size() >= INDEX_W * INDEX_H){
                stopUpdateTimer();
                boolean isGameOver = true;
                OnGameStatusChangedListener _OnGameStatusChangedListener = mOnGameStatusChangedListener;
                mOnGameStatusChangedListener = null;
                for(int i = 0; i < 4; i++){
                    if(handleTouchEvent(mCubeList, i, true)){
                        isGameOver = false;
                        break;
                    }
                }
                mOnGameStatusChangedListener = _OnGameStatusChangedListener;
                if(isGameOver){
                    synchronized (TwoGameView.class){
                        if(mOnGameStatusChangedListener != null){
                            mOnGameStatusChangedListener.checkFail();
                            mOnGameStatusChangedListener.completeOnRound(false);
                        }
                        mOnGameStatusChangedListener = null;
                    }
                }else{
                    startUpdateTimer();
                }
                Log.d(TAG, "check Over");
                addOne = false;
            }else if(haveChanged && MODE != MODE_CRAZY){
                CubeRect cr = randomCubeRect();
                if(cr != null){
                    mCubeList.add(cr);
                    addOne = true;
                }
            }
            haveChanged = false;
        }

        if(mCubeList.size() < INDEX_W * INDEX_H && MODE == MODE_CRAZY){
            crazy_mode_time += mPeriod;
            if(crazy_mode_time > crazy_mode_add_time){
                crazy_mode_time = 0;

                CubeRect cr = randomCubeRect();
                if(cr != null){
                    mCubeList.add(cr);
                    addOne = true;
                }
            }
        }
        return allOver;
    }

    private void initializeData(){
        mCubeList = new ArrayList<CubeRect>();
        if(MODE == MODE_TIME){
            mCubeList.add(randomCubeRect(1024));
            mCubeList.add(randomCubeRect());
        }else if(MODE == MODE_OBSTACLE){
            CubeRect cr = randomCubeRect(0);
            if(cr != null)
                mCubeList.add(cr);
            for(int i = 0; i < 2; i++){
                cr = randomCubeRect();
                if(cr != null)
                    mCubeList.add(cr);
            }
        }else{
            for(int i = 0; i < 2; i++){
                CubeRect cr = randomCubeRect();
                if(cr != null)
                    mCubeList.add(cr);
            }
        }
    }

    public String getDataString(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(CubeRect cr : mCubeList){
            try {
                jsonArray.put(new JSONObject(cr.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObject.put(KEY_INDEX_W, INDEX_W);
            jsonObject.put(KEY_INDEX_H, INDEX_H);
            jsonObject.put(KEY_DATA, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void setData(String info){
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(info);
            jsonArray = jsonObject.optJSONArray(KEY_DATA);
            INDEX_W = jsonObject.optInt(KEY_INDEX_W);
            INDEX_H = jsonObject.optInt(KEY_INDEX_H);
        } catch (JSONException e) {
            e.printStackTrace();
            INDEX_W = 4;
            INDEX_H = 4;
            return;
        }

        mCubeList = new ArrayList<CubeRect>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jo = jsonArray.optJSONObject(i);
            mCubeList.add(CubeRect.parse(jo.toString()));
        }
        isInit = true;
    }

    public int getIndexW() {
        return INDEX_W;
    }

    public int getIndexH() {
        return INDEX_H;
    }

    private CubeRect randomCubeRect(String num){
        if(mCubeList.size() >= INDEX_H * INDEX_W)
            return null;
        CubeRect[][] cubeRectList = new CubeRect[INDEX_H][INDEX_W];

        for(CubeRect cr : mCubeList){
            cubeRectList[cr.getFinalIndexY()][cr.getFinalIndexX()] = cr;
        }

        int newIndexX;
        int newIndexY;
        do{
            newIndexX = mRandom.nextInt(INDEX_W);
            newIndexY = mRandom.nextInt(INDEX_H);
        }while (cubeRectList[newIndexY][newIndexX] != null);

        RectF rectF = new RectF(0, 0,
                mCanvasRectF.width() / INDEX_W,
                mCanvasRectF.height() / INDEX_H);

        if(num == null)
            if(MODE == MODE_OBSTACLE && mRandom.nextFloat() > 0.98){
                num = "0";
            }else{
                num = String.valueOf((mRandom.nextInt(2) + 1) * 2);
            }

        return new CubeRect(num + "",
                newIndexX,
                newIndexY,
                rectF,
                mCubePadding,
                mCanvasRectF);
    }

    private CubeRect randomCubeRect(){
        return randomCubeRect(null);
    }

    private CubeRect randomCubeRect(int num){
        return randomCubeRect(String.valueOf(num));
    }

    private float indexXToPosition(int indexX){
        return mCanvasRectF.left + mCanvasRectF.width() / INDEX_W * indexX;
    }

    private float indexYToPosition(int indexY){
        return mCanvasRectF.top + mCanvasRectF.height() / INDEX_H * indexY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCanvasRectF.inset(-mCubePadding, -mCubePadding);
        canvas.drawRoundRect(mCanvasRectF, mCanvasRectF.width() / 30, mCanvasRectF.width() / 30, mCanvasRectPaint);
        mCanvasRectF.inset(mCubePadding, mCubePadding);
        for(int i = 0; i < INDEX_W; i++)
            for (int j = 0; j < INDEX_H; j++){
                mBackRectF.offsetTo(indexXToPosition(i) + mCubePadding, indexYToPosition(j) + mCubePadding);
                canvas.drawRoundRect(mBackRectF, mBackRectF.width() / 8, mBackRectF.height() / 8, mBackPaint);
            }
        for(int i = 0; i < mCubeList.size(); i++){
            CubeRect cr = mCubeList.get(i);
            cr.drawMySelf(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG, "DOWN");
                startTouch = true;
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "MOVE");
                if(!isStart){
                    isStart = true;
                    if(mOnGameStatusChangedListener != null)
                        mOnGameStatusChangedListener.startGame();
                }
                if(!startTouch || FloatMath.sqrt((startX - x) * (startX - x) + (startY - y) * (startY - y)) < mWidth / 26)
                    return true;
                startTouch = false;
//                if(FloatMath.sqrt(startX * x + startY * y) < mWidth / 30)
//                    return true;
                int touchDirection = -1;
                float offsetX = x - startX;
                float offsetY = y - startY;
                Log.d(TAG, "offsetX = " + offsetX + "; offsetY = " + offsetY);
                Log.d(TAG, "x = " + x + "; y = " + y);
                Log.d(TAG, "startX = " + startX + "; startY = " + startY);
                Log.d(TAG, "sqrt = " + FloatMath.sqrt(startX * x + startY * y));
                if(offsetY > 0 && offsetY > Math.abs(offsetX)){
                    Log.d(TAG, "down 3");
                    touchDirection = 3;
                }else if(offsetY < 0 && -offsetY > Math.abs(offsetX)) {
                    Log.d(TAG, "up 1");
                    touchDirection = 1;
                }else if(offsetX > 0 && offsetX > Math.abs(offsetY)) {
                    Log.d(TAG, "right 2");
                    touchDirection = 2;
                }else if(offsetX < 0 && -offsetX > Math.abs(offsetY)) {
                    Log.d(TAG, "left 0");
                    touchDirection = 0;
                }

                boolean canChange = false;
                do{
                    canChange = false;
                    for(CubeRect cr : mCubeList)
                        if(cr.getCurrentStatus().getStatus() != StatusBase.STATUS_STOP)
                            canChange = true;

                    try {
                        if(canChange)
                            Thread.sleep(mPeriod);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while (canChange);

//                for(CubeRect cr : mCubeList)
//                    if(cr.getCurrentStatus().getStatus() != StatusBase.STATUS_STOP)
//                        return true;

                stopUpdateTimer();
                haveChanged = handleTouchEvent(mCubeList, touchDirection, false);
                startUpdateTimer();
        }

        return true;
    }

    private PointF indexToPosition(int indexX, int indexY){
        return new PointF(mCanvasRectF.left + mCanvasRectF.width() / INDEX_W * indexX,
                mCanvasRectF.top + mCanvasRectF.height() / INDEX_H * indexY);
    }

    private boolean handleTouchEvent(List<CubeRect> cubeList, int touchDirection, boolean isTest) {
        CubeRect[][] cubeRectList = new CubeRect[INDEX_H][INDEX_W];

        for(int i = 0; i < cubeList.size(); i++){
            CubeRect cr = cubeList.get(i);
            if(cubeRectList[cr.getNowIndexY()][cr.getNowIndexX()] != null){
                Log.d(TAG, "有重复");
//                CubeRect _cr = cubeRectList[cr.getNowIndexY()][cr.getNowIndexX()];
//                if(_cr.getNum() > cr.getNum()){
//                    cubeList.remove(cr);
//                }else{
//                    cubeList.remove(_cr);
//                }
            }
            cubeRectList[cr.getNowIndexY()][cr.getNowIndexX()] = cr;
        }

        boolean _haveChanged = false;
        haveMerge = false;
        haveMove = false;

        switch (touchDirection){
            case DIRECTION_LEFT:
                for(int column = 0; column < INDEX_H; column++){
                    //合并
                    for(int row = 0; row < INDEX_W; row++){
                        if(cubeRectList[column][row] != null){

                            CubeRect findOneCube = cubeRectList[column][row];

                            for(int i = row + 1; i < INDEX_W; i++)
                                if(cubeRectList[column][i] != null){
                                    if(findOneCube.getNum() == cubeRectList[column][i].getNum()){
                                        if(isTest)
                                            return true;
                                        haveMove = true;
                                        haveMerge = true;

                                        int indexX = findOneCube.getNowIndexX();
                                        int indexY = findOneCube.getNowIndexY();

                                        findOneCube.addStatus(new MergeStatus(indexX, indexY));
                                        if(mOnGameStatusChangedListener != null)
                                            mOnGameStatusChangedListener.checkSuccess(findOneCube.getNum() * 2);

                                        cubeRectList[column][i].addStatus(new DieStatus(indexX, indexY));
                                        cubeRectList[column][i].addStatus(new MoveStatus(indexX, indexY));
                                        cubeRectList[column][i].setFinalIndex(indexX, indexY);

                                        row = i;
                                        _haveChanged = true;
                                    }
                                    break;
                                }
                        }
                    }

                    //平移
                    int index = 0;
                    CubeRect[] columnCube = new CubeRect[INDEX_W];
                    for(int row = 0; row < INDEX_W; row++){
                        if(cubeRectList[column][row] != null){
                            CubeRect findOneCube = cubeRectList[column][row];
//                            if(findOneCube.willBeDie())
//                                index--;

                            columnCube[index] = findOneCube;
                            if(findOneCube.getNowIndexX() != index){
                                if(isTest)
                                    return true;
                                haveMove = true;

                                findOneCube.addStatus(new MoveStatus(index, column));
                                _haveChanged = true;
                                if(findOneCube.willBeDie()){
                                    findOneCube.setFinalIndex(index - 1, column);
                                }else{
                                    findOneCube.setFinalIndex(index, column);
                                }
                            }
                            if(!cubeRectList[column][row].willBeDie())
                                index++;
                        }
                    }
                    cubeRectList[column] = columnCube;
                }
                break;
            case DIRECTION_RIGHT:
                for(int column = 0; column < INDEX_H; column++){
                    //合并
                    for(int row = INDEX_W - 1; row >= 0; row--){
                        if(cubeRectList[column][row] != null){

                            CubeRect findOneCube = cubeRectList[column][row];

                            for(int i = row - 1; i >= 0; i--)
                                if(cubeRectList[column][i] != null){
                                    if(findOneCube.getNum() == cubeRectList[column][i].getNum()){
                                        if(isTest)
                                            return true;
                                        haveMove = true;
                                        haveMerge = true;

                                        int indexX = findOneCube.getNowIndexX();
                                        int indexY = findOneCube.getNowIndexY();

                                        findOneCube.addStatus(new MergeStatus(indexX, indexY));
                                        if(mOnGameStatusChangedListener != null)
                                            mOnGameStatusChangedListener.checkSuccess(findOneCube.getNum() * 2);

                                        cubeRectList[column][i].addStatus(new DieStatus(indexX, indexY));
                                        cubeRectList[column][i].addStatus(new MoveStatus(indexX, indexY));
                                        cubeRectList[column][i].setFinalIndex(indexX, indexY);

                                        row = i;
                                        _haveChanged = true;
                                    }
                                    break;
                                }
                        }
                    }

                    //平移
                    int index = INDEX_W - 1;
                    CubeRect[] columnCube = new CubeRect[INDEX_W];
                    for(int row = INDEX_W - 1; row >= 0; row--){
                        if(cubeRectList[column][row] != null){
                            CubeRect findOneCube = cubeRectList[column][row];
//                            if(findOneCube.willBeDie())
//                                index++;

                            columnCube[index] = findOneCube;
                            if(findOneCube.getNowIndexX() != index){
                                if(isTest)
                                    return true;
                                haveMove = true;

                                findOneCube.addStatus(new MoveStatus(index, column));
                                _haveChanged = true;
                                if(findOneCube.willBeDie()){
                                    findOneCube.setFinalIndex(index + 1, column);
                                }else{
                                    findOneCube.setFinalIndex(index, column);
                                }
                            }
                            if(!cubeRectList[column][row].willBeDie())
                                index--;
                        }
                    }
                    cubeRectList[column] = columnCube;
                }
                break;
            case DIRECTION_UP:
                for(int row = 0; row < INDEX_W; row++){
                    //合并
                    for(int column = 0; column < INDEX_H; column++){
                        if(cubeRectList[column][row] != null){

                            CubeRect findOneCube = cubeRectList[column][row];

                            for(int i = column + 1; i < INDEX_H; i++)
                                if(cubeRectList[i][row] != null){
                                    if(findOneCube.getNum() == cubeRectList[i][row].getNum()){
                                        if(isTest)
                                            return true;
                                        haveMove = true;
                                        haveMerge = true;

                                        int indexX = findOneCube.getNowIndexX();
                                        int indexY = findOneCube.getNowIndexY();

                                        findOneCube.addStatus(new MergeStatus(indexX, indexY));
                                        if(mOnGameStatusChangedListener != null)
                                            mOnGameStatusChangedListener.checkSuccess(findOneCube.getNum() * 2);

                                        cubeRectList[i][row].addStatus(new DieStatus(indexX, indexY));
                                        cubeRectList[i][row].addStatus(new MoveStatus(indexX, indexY));
                                        cubeRectList[i][row].setFinalIndex(indexX, indexY);

                                        column = i;
                                        _haveChanged = true;
                                    }
                                    break;
                                }
                        }
                    }

                    //平移
                    int index = 0;
                    CubeRect[] columnCube = new CubeRect[INDEX_H];
                    for(int column = 0; column < INDEX_H; column++){
                        if(cubeRectList[column][row] != null){
                            CubeRect findOneCube = cubeRectList[column][row];
//                            if(findOneCube.willBeDie())
//                                index--;

                            columnCube[index] = findOneCube;
                            if(findOneCube.getNowIndexY() != index){
                                if(isTest)
                                    return true;
                                haveMove = true;

                                findOneCube.addStatus(new MoveStatus(row, index));
                                _haveChanged = true;
                                if(findOneCube.willBeDie()){
                                    findOneCube.setFinalIndex(row, index - 1);
                                }else{
                                    findOneCube.setFinalIndex(row, index);
                                }
                            }
                            if(!cubeRectList[column][row].willBeDie())
                                index++;
                        }
                    }
                    //TODO cubeRectList[row] = columnCube;
                }
                break;
            case DIRECTION_DOWN:
                for(int row = 0; row < INDEX_W; row++){
                    //合并
                    for(int column = INDEX_H - 1; column >= 0; column--){
                        if(cubeRectList[column][row] != null){

                            CubeRect findOneCube = cubeRectList[column][row];

                            for(int i = column - 1; i >= 0; i--)
                                if(cubeRectList[i][row] != null){
                                    if(findOneCube.getNum() == cubeRectList[i][row].getNum()){
                                        if(isTest)
                                            return true;
                                        haveMove = true;
                                        haveMerge = true;

                                        int indexX = findOneCube.getNowIndexX();
                                        int indexY = findOneCube.getNowIndexY();

                                        findOneCube.addStatus(new MergeStatus(indexX, indexY));
                                        if(mOnGameStatusChangedListener != null)
                                            mOnGameStatusChangedListener.checkSuccess(findOneCube.getNum() * 2);

                                        cubeRectList[i][row].addStatus(new DieStatus(indexX, indexY));
                                        cubeRectList[i][row].addStatus(new MoveStatus(indexX, indexY));
                                        cubeRectList[i][row].setFinalIndex(indexX, indexY);

                                        column = i;
                                        _haveChanged = true;
                                    }
                                    break;
                                }
                        }
                    }

                    //平移
                    int index = INDEX_H - 1;
                    CubeRect[] columnCube = new CubeRect[INDEX_H];
                    for(int column = INDEX_H - 1; column >= 0; column--){
                        if(cubeRectList[column][row] != null){
                            CubeRect findOneCube = cubeRectList[column][row];
//                            if(findOneCube.willBeDie())
//                                index++;

                            columnCube[index] = findOneCube;
                            if(findOneCube.getNowIndexY() != index){
                                if(isTest)
                                    return true;
                                haveMove = true;

                                findOneCube.addStatus(new MoveStatus(row, index));
                                _haveChanged = true;

                                if(findOneCube.willBeDie()){
                                    findOneCube.setFinalIndex(row, index + 1);
                                }else{
                                    findOneCube.setFinalIndex(row, index);
                                }
                            }
                            if(!cubeRectList[column][row].willBeDie())
                                index--;
                        }
                    }
                    //TODO cubeRectList[row] = columnCube;
                }
                break;
        }

        for(CubeRect cr : cubeList){
            cr.arrangeStatusList();
        }
        Collections.sort(cubeList);
        return _haveChanged;
    }

}
