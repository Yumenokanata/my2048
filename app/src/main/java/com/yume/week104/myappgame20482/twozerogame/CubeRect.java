package com.yume.week104.myappgame20482.twozerogame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.yume.week104.myappgame20482.twozerogame.Status.StartStatus;
import com.yume.week104.myappgame20482.twozerogame.Status.StatusBase;
import com.yume.week104.myappgame20482.twozerogame.Status.StopStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin1 on 15-6-11.
 */
public class CubeRect implements Comparable {
    private static final String TAG = "CubeRect";

    private static final HashMap<String, int[]> colorMap = new HashMap<String, int[]>();
    static {
        colorMap.put("0", new int[]{0xFF21C670, Color.WHITE});
        colorMap.put("2", new int[]{0xffEEE4DA, 0xff776D63});
        colorMap.put("4", new int[]{0xffECE0CA, 0xff7C7268});
        colorMap.put("8", new int[]{0xffF2B17A, Color.WHITE});
        colorMap.put("16", new int[]{0xffF59663, Color.WHITE});
        colorMap.put("32", new int[]{0xffF57D62, Color.WHITE});
        colorMap.put("64", new int[]{0xffFA5E3D, Color.WHITE});
        colorMap.put("128", new int[]{0xffEDCE72, Color.WHITE});
        colorMap.put("256", new int[]{0xffEDCC62, Color.WHITE});
        colorMap.put("512", new int[]{0xffECC851, Color.WHITE});
        colorMap.put("1024", new int[]{0xffEDC540, Color.WHITE});
        colorMap.put("2048", new int[]{0xffEDB439, Color.WHITE});
        colorMap.put("4096", new int[]{0xff5CB4EE, Color.BLACK});
        colorMap.put("8192", new int[]{0xff5ab4f2, Color.WHITE});
        colorMap.put("16384", new int[]{0xff528aeb, Color.WHITE});
        colorMap.put("32768", new int[]{0xff4d62ef, Color.WHITE});
    }

    List<StatusBase> StatusList;
    RectF mOriginRectF;
    RectF mDrawRectF;
    RectF mCanvasRectF;
    Paint mRectPaint;
    Paint mTextPaint;

    float mCubePadding;

    PointF tPoint = new PointF();

    int nowIndexX;
    int nowIndexY;
    int nowFrameNum = 0;
    int sumFrameNum = 5;
    float nowX;
    float nowY;

    String mText;
    float textOffsetX;
    float textOffsetY;

    CubeRect(String text, int indexX, int indexY, RectF originRectF, float cubePadding, RectF canvasRectF){
        StatusList = new ArrayList<StatusBase>();
        StatusList.add(0, new StopStatus(indexX, indexY));
        StatusList.add(0, new StartStatus(indexX, indexY));

        mCubePadding = cubePadding;

        mCanvasRectF = canvasRectF;

        mOriginRectF = originRectF;
        mDrawRectF = new RectF(originRectF);

        mRectPaint = new Paint();
        mRectPaint.setColor(colorMap.get(text)[0]);
        mRectPaint.setAlpha(0);

        mTextPaint = new Paint();
        mTextPaint.setColor(colorMap.get(text)[1]);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setAlpha(0);

        mText = text;
        measureText();

        nowIndexX = indexX;
        nowIndexY = indexY;
        nowX = indexXToPosition(nowIndexX);
        nowY = indexYToPosition(nowIndexY);
    }

    private void measureText(){
        if(mText.length() <= 1){
            mTextPaint.setTextSize(mOriginRectF.width() / 2);
        }else{
            mTextPaint.setTextSize(mOriginRectF.width() / 2);
            float textWidth = mTextPaint.measureText(mText);
            float scale = textWidth / (mOriginRectF.width() - 2 * mCubePadding - mOriginRectF.width() * 2 / 10);
            mTextPaint.setTextSize(mOriginRectF.width() / 2 / scale);
        }

        float textWidth = mTextPaint.measureText(mText);
        textOffsetX = (mOriginRectF.width() - textWidth) / 2;
        Paint.FontMetrics ft = mTextPaint.getFontMetrics();
        float textHeight = ft.bottom - ft.ascent;
        textOffsetY = (mOriginRectF.height() - textHeight) / 2 - ft.ascent;
    }

    public void reMeasure(RectF canvasRectF, float width, float height) {
        this.mCanvasRectF = canvasRectF;
        this.mOriginRectF.set(0, 0, width, height);
        measureText();
    }

    public void addStatus(StatusBase statusBase){
        StatusList.add(0, statusBase);
    }

    public boolean willBeDie(){
        for(StatusBase sb : StatusList)
            if(sb.getStatus() == StatusBase.STATUS_DIE)
                return true;
        return false;
    }

    public boolean willBeMerge(){
        for(StatusBase sb : StatusList)
            if(sb.getStatus() == StatusBase.STATUS_MERGE)
                return true;
        return false;
    }

    public void setFinalIndex(int indexX, int indexY){
        StatusList.get(StatusList.size() - 1).setIndexX(indexX);
        StatusList.get(StatusList.size() - 1).setIndexY(indexY);
    }

    public int getFinalIndexX(){
        return StatusList.get(StatusList.size() - 1).getIndexX();
    }

    public int getFinalIndexY(){
        return StatusList.get(StatusList.size() - 1).getIndexY();
    }

    public void arrangeStatusList(){
        if(StatusList.size() <= 1)
            return;
        int indexX = StatusList.get(StatusList.size() - 1).getIndexX();
        int indexY = StatusList.get(StatusList.size() - 1).getIndexY();
        StatusBase _sb = null;
        for(int i = 0; i < StatusList.size(); i++){
            StatusBase sb = StatusList.get(i);
            if(_sb != null && _sb != sb && _sb.getStatus() == sb.getStatus()){
                StatusList.remove(i);
                i--;
                continue;
            }
            sb.setIndexX(indexX);
            sb.setIndexY(indexY);
            _sb = sb;
        }
    }

    public int getNum() {
        return Integer.valueOf(mText);
    }

    public void setNum(int num) {
        this.mText = String.valueOf(num);

        if(Integer.valueOf(mText) > 32768){
            mRectPaint.setColor(Color.BLACK);
            mTextPaint.setColor(Color.WHITE);
        }else{
            mRectPaint.setColor(colorMap.get(mText)[0]);
            mTextPaint.setColor(colorMap.get(mText)[1]);
        }
        measureText();
    }

    public StatusBase getCurrentStatus(){
        return StatusList.get(0);
    }

    public boolean isDrawOver(){
        return nowFrameNum == 0 || nowFrameNum == -1;
    }

    public void updateStatus(){
        StatusBase statusBase = StatusList.get(0);

        if(nowFrameNum == -1 || statusBase.getStatus() == StatusBase.STATUS_STOP)
            return;

        nowFrameNum++;
        if(nowFrameNum > sumFrameNum){
            switch (statusBase.getStatus()){
                case StatusBase.STATUS_DIE:
                    nowFrameNum = -1;

                    mRectPaint.setAlpha(0);
                    mTextPaint.setAlpha(0);
                    mDrawRectF.set(mOriginRectF);
                    nowIndexX = statusBase.getIndexX();
                    nowIndexY = statusBase.getIndexY();
                    nowX = indexXToPosition(nowIndexX);
                    nowY = indexYToPosition(nowIndexY);
                    break;
                case StatusBase.STATUS_MOVE:
                    nowFrameNum = 0;
                    StatusList.remove(0);

                    if(StatusList.get(0).getStatus() == StatusBase.STATUS_STOP){
                        StatusList.get(0).setIndexX(statusBase.getIndexX());
                        StatusList.get(0).setIndexY(statusBase.getIndexY());
                    }

                    mRectPaint.setAlpha(255);
                    mTextPaint.setAlpha(255);
                    mDrawRectF.set(mOriginRectF);
                    nowIndexX = statusBase.getIndexX();
                    nowIndexY = statusBase.getIndexY();
                    nowX = indexXToPosition(nowIndexX);
                    nowY = indexYToPosition(nowIndexY);
                    break;
                default:
                    nowFrameNum = 0;
                    StatusList.remove(0);

                    if(StatusList.get(0).getStatus() == StatusBase.STATUS_STOP){
                        StatusList.get(0).setIndexX(statusBase.getIndexX());
                        StatusList.get(0).setIndexY(statusBase.getIndexY());
                    }

                    mRectPaint.setAlpha(255);
                    mTextPaint.setAlpha(255);
                    mDrawRectF.set(mOriginRectF);
                    break;
            }
            return;
        }

        switch (statusBase.getStatus()){
            case StatusBase.STATUS_DIE:
//                mRectPaint.setAlpha((int) (255 - 255.0f / sumFrameNum * nowFrameNum));
//                mTextPaint.setAlpha((int) (255 - 255.0f / sumFrameNum * nowFrameNum));
//
//                mDrawRectF.inset(mOriginRectF.width() / 16, mOriginRectF.height() / 16);
                mRectPaint.setAlpha(0);
                mTextPaint.setAlpha(0);
                nowFrameNum = sumFrameNum;
                break;
            case StatusBase.STATUS_MERGE:
                if(nowFrameNum < sumFrameNum / 2){
                    mDrawRectF.inset(mOriginRectF.width() / 20, mOriginRectF.height() / 20);
                }else{
                    mDrawRectF.inset(-mOriginRectF.width() / 20, -mOriginRectF.height() / 20);
                }
                if(nowFrameNum == 1){
                    setNum(Integer.valueOf(mText) * 2);
                }
                break;
            case StatusBase.STATUS_MOVE:
                nowX = indexXToPosition(nowIndexX) + (indexXToPosition(statusBase.getIndexX()) - indexXToPosition(nowIndexX)) / sumFrameNum * nowFrameNum;
                nowY = indexYToPosition(nowIndexY) + (indexYToPosition(statusBase.getIndexY()) - indexYToPosition(nowIndexY)) / sumFrameNum * nowFrameNum;
                break;
            case StatusBase.STATUS_START:
                int alpha = (int) (255.0f / sumFrameNum * nowFrameNum);
                mRectPaint.setAlpha(alpha);
                mTextPaint.setAlpha(alpha);

                if(nowFrameNum == 1){
                    mDrawRectF.inset(mOriginRectF.width() / 12, mOriginRectF.height() / 12);
                }
                mDrawRectF.inset(-mOriginRectF.width() / 12 / sumFrameNum, -mOriginRectF.height() / 12 / sumFrameNum);
                break;
        }
    }

    public int getNowIndexX() {
        return nowIndexX;
    }

    public int getNowIndexY() {
        return nowIndexY;
    }

    private PointF indexToPosition(int indexX, int indexY){
        tPoint.set(mCanvasRectF.left + mCanvasRectF.width() / TwoGameView.INDEX_W * indexX,
                mCanvasRectF.top + mCanvasRectF.height() / TwoGameView.INDEX_H * indexY);
        return tPoint;
    }

    private float indexXToPosition(int indexX){
        return mCanvasRectF.left + mCanvasRectF.width() / TwoGameView.INDEX_W * indexX;
    }

    private float indexYToPosition(int indexY){
        return mCanvasRectF.top + mCanvasRectF.height() / TwoGameView.INDEX_H * indexY;
    }

    public void drawMySelf(Canvas canvas){
        mDrawRectF.offsetTo(nowX - (mDrawRectF.width() - mOriginRectF.width()) / 2, nowY - (mDrawRectF.height() - mOriginRectF.height()) / 2);
        mDrawRectF.inset(mCubePadding, mCubePadding);
        canvas.drawRoundRect(mDrawRectF, mDrawRectF.width() / 8, mDrawRectF.width() / 8, mRectPaint);
        mDrawRectF.inset(-mCubePadding, -mCubePadding);

        canvas.drawText(mText, nowX + textOffsetX, nowY + textOffsetY, mTextPaint);
    }

    @Override
    public int compareTo(Object another) {
        CubeRect cube = (CubeRect) another;
        Integer my = Integer.valueOf(mText);
        Integer you = cube.getNum();
        if(willBeMerge())
            my *= 2;
        if(cube.willBeMerge())
            you *= 2;
        return my.compareTo(you);
    }

    private static final String KEY_TEXT = "text";
    private static final String KEY_INDEX_X = "indexX";
    private static final String KEY_INDEX_Y = "indexY";
    private static final String KEY_ORIGIN_RECT_W = "originRectW";
    private static final String KEY_ORIGIN_RECT_H = "originRectH";
    private static final String KEY_CUBE_PADDING = "cubePadding";
    private static final String KEY_CANVAS_RECT_X0 = "canvasRectF_x0";
    private static final String KEY_CANVAS_RECT_Y0 = "canvasRectF_y0";
    private static final String KEY_CANVAS_RECT_X1 = "canvasRectF_x1";
    private static final String KEY_CANVAS_RECT_Y1 = "canvasRectF_y1";

    public static CubeRect parse(String info){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        String text = jsonObject.optString(KEY_TEXT);
        int indexX = jsonObject.optInt(KEY_INDEX_X);
        int indexY = jsonObject.optInt(KEY_INDEX_Y);
        RectF originRectF = new RectF(0, 0,
                jsonObject.optLong(KEY_ORIGIN_RECT_W),
                jsonObject.optLong(KEY_ORIGIN_RECT_H));
        float cubePadding = jsonObject.optInt(KEY_CUBE_PADDING);
        RectF canvasRectF = new RectF(
                jsonObject.optLong(KEY_CANVAS_RECT_X0),
                jsonObject.optLong(KEY_CANVAS_RECT_Y0),
                jsonObject.optLong(KEY_CANVAS_RECT_X1),
                jsonObject.optLong(KEY_CANVAS_RECT_Y1));
        return new CubeRect(text, indexX, indexY, originRectF, cubePadding, canvasRectF);
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_TEXT, mText);
            jsonObject.put(KEY_INDEX_X, nowIndexX);
            jsonObject.put(KEY_INDEX_Y, nowIndexY);
            jsonObject.put(KEY_ORIGIN_RECT_W, mOriginRectF.width());
            jsonObject.put(KEY_ORIGIN_RECT_H, mOriginRectF.height());
            jsonObject.put(KEY_CUBE_PADDING, mCubePadding);
            jsonObject.put(KEY_CANVAS_RECT_X0, mCanvasRectF.left);
            jsonObject.put(KEY_CANVAS_RECT_Y0, mCanvasRectF.top);
            jsonObject.put(KEY_CANVAS_RECT_X1, mCanvasRectF.right);
            jsonObject.put(KEY_CANVAS_RECT_Y1, mCanvasRectF.bottom);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject.toString();
    }
}
