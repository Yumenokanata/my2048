package com.yume.week104.myappgame20482;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin1 on 15-6-1.
 */
public abstract class GameView extends View {
    protected Context mContext;
    protected OnGameStatusChangedListener mOnGameStatusChangedListener = null;
    protected double speed = 0;
    protected double memory = 0;
    protected double accuracy = 0;
    protected double judgement = 0;
    protected double calculation = 0;
    protected double observation = 0;

    public GameView(Context context) {
        super(context);
        mContext = context;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public static String randomProductionSeed(){
        return String.valueOf(System.nanoTime());
    }
//    public UserInfo setUserScore(UserInfo userInfo, float rightRatio) {
//        userInfo.speed = (int) (userInfo.speed + speed *((userInfo.speed + rightRatio * 100) / 2 - userInfo.speed));
//        userInfo.memory = (int) (userInfo.memory + memory *((userInfo.memory + rightRatio * 100) / 2 - userInfo.memory));
//        userInfo.accuracy = (int) (userInfo.accuracy + accuracy *((userInfo.accuracy + rightRatio * 100) / 2 - userInfo.accuracy));
//        userInfo.judgement = (int) (userInfo.judgement + judgement *((userInfo.judgement + rightRatio * 100) / 2 - userInfo.judgement));
//        userInfo.calculation = (int) (userInfo.calculation + calculation *((userInfo.calculation + rightRatio * 100) / 2 - userInfo.calculation));
//        userInfo.observation = (int) (userInfo.observation + observation *((userInfo.observation + rightRatio * 100) / 2 - userInfo.observation));
//
//        userInfo.speed = Math.min(userInfo.speed, 100);
//        userInfo.memory = Math.min(userInfo.memory, 100);
//        userInfo.accuracy = Math.min(userInfo.accuracy, 100);
//        userInfo.judgement = Math.min(userInfo.judgement, 100);
//        userInfo.calculation = Math.min(userInfo.calculation, 100);
//        userInfo.observation = Math.min(userInfo.observation, 100);
//
//        userInfo.speed = Math.max(userInfo.speed, 0);
//        userInfo.memory = Math.max(userInfo.memory, 0);
//        userInfo.accuracy = Math.max(userInfo.accuracy, 0);
//        userInfo.judgement = Math.max(userInfo.judgement, 0);
//        userInfo.calculation = Math.max(userInfo.calculation, 0);
//        userInfo.observation = Math.max(userInfo.observation, 0);
//        userInfo.sum_score += rightRatio * 100;
//        return userInfo;
//    }

    public abstract void setSeed(String seed);
    public abstract void reset();

    public abstract void startUpdateTimer();
    public abstract void stopUpdateTimer();

    public void setOnGameStatusChangedListener(GameView.OnGameStatusChangedListener onGameStatusChangedListener){
        this.mOnGameStatusChangedListener = onGameStatusChangedListener;
    }

    public interface OnGameStatusChangedListener{
        void checkFail();
        void checkSuccess();
        void checkSuccess(int score);
        void completeOnRound();
    }

//    public static final GameView factoryGameView(int gameClass, Context context){
//        switch (gameClass){
//            case 0:
//                return new CardGameView(context);
//            case 1:
//                return new buttonn(context);
//            case 2:
//                return new OrderDestroyGameView(context);
//            case 3:
//                return new DirectionGameView(context);
//            case 4:
//                return new shandian(context);
//            case 5:
//                return new ColorSwitchView(context);
//            case 6:
//                return new ColorTrap(context);
//        }
//        return null;
//    }
}
