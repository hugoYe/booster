package com.cooeeui.nanobooster.views;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.cooeeui.nanobooster.MainActivity;
import com.cooeeui.nanobooster.R;

import java.util.Timer;
import java.util.TimerTask;

public class MyWidget extends AppWidgetProvider {
	  @Override    
	    public void onUpdate(Context context, AppWidgetManager appWidgetManager,     
	            int[] appWidgetIds) {     
	             
	        Timer timer = new Timer();     
	        timer.scheduleAtFixedRate(new MyTime(context,appWidgetManager), 1, 60000);

              for(int i= 0;i<appWidgetIds.length;i++){
                  //新intent
                  Intent intent = new Intent(context,MainActivity.class);
                  PendingIntent pendingIntent = PendingIntent.getActivity(
                      context, 0, intent, 0);
                  //创建一个remoteViews。
                  RemoteViews remoteViews  = new RemoteViews(
                      context.getPackageName(), R.layout.widget_view);
                  //绑定处理器，表示控件单击后，会启动pendingIntent。
                  remoteViews.setOnClickPendingIntent(R.id.ll_widget, pendingIntent);
                  appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
              }

	        super.onUpdate(context, appWidgetManager, appWidgetIds);

	    }     
	         
	         
	    private class MyTime extends TimerTask{     
	        RemoteViews remoteViews;     
	        AppWidgetManager appWidgetManager;     
	        ComponentName thisWidget;     
	             
	        public MyTime(Context context,AppWidgetManager appWidgetManager){     
	            this.appWidgetManager = appWidgetManager;     
	            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_view);
	                 
	            thisWidget = new ComponentName(context,MyWidget.class);
	        }     
	        public void run() {     
	                 
	          //  Date date = new Date();
	          //  Calendar calendar = new GregorianCalendar(2010,06,11);
	           // long days = (((calendar.getTimeInMillis()-date.getTime())/1000))/86400;
	          //  remoteViews.setImageViewResource(R.id.iv_widget,);
	            appWidgetManager.updateAppWidget(thisWidget, remoteViews);     
	        }
	             
	    }

}
