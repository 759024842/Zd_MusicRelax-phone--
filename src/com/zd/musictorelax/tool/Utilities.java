package com.zd.musictorelax.tool;

public class Utilities {
	/**
     * 当前毫秒转时间
     * */
    public String milliSecondsToTimer(long milliseconds)
    {
        String finalTimerString ="";
        String secondsString ="";
        // Convert total duration into time
           int hours = (int)( milliseconds / (1000*60*60));
           int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
           int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
           // Add hours if there
           if(hours > 0){
               finalTimerString = hours +":";
           }
           // Prepending 0 to seconds if it is one digit
           if(seconds < 10)
               secondsString ="0"+ seconds;
           else
               secondsString =""+ seconds;
           finalTimerString = finalTimerString + minutes +":"+ secondsString;
        return finalTimerString;
    }
    /**
     * 通过进度条当前进度与总进度转换出进度值(最大长度为100)
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration,long totalDuration){
        Double percentage = (double)0;
        long currentSeconds = (int) (currentDuration / 1000);//当前进度转换成秒
        long totalSeconds = (int) (totalDuration / 1000);//总持续时间转换成秒
        percentage =(((double)currentSeconds)/totalSeconds)*100;//转换成进度值
        return percentage.intValue();
    }
    /**
     * 进度值转换成时间
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress,int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
