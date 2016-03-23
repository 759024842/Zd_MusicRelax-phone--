package com.zd.musictorelax.tool;

public class Utilities {
	/**
     * ��ǰ����תʱ��
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
     * ͨ����������ǰ�������ܽ���ת��������ֵ(��󳤶�Ϊ100)
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration,long totalDuration){
        Double percentage = (double)0;
        long currentSeconds = (int) (currentDuration / 1000);//��ǰ����ת������
        long totalSeconds = (int) (totalDuration / 1000);//�ܳ���ʱ��ת������
        percentage =(((double)currentSeconds)/totalSeconds)*100;//ת���ɽ���ֵ
        return percentage.intValue();
    }
    /**
     * ����ֵת����ʱ��
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
