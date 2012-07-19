package com.adtworker.snake;

import android.view.Display;
import android.view.WindowManager;

public class Constants {

    private static Integer screenWidth;
    private static Integer screenHeight;

    public static void setDisplay(WindowManager windowManager) {
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }

    public static Integer getScreenWidth() {
        return screenWidth;
    }

    public static void setScreenWidth(Integer screenWidth) {
        Constants.screenWidth = screenWidth;
    }

    public static Integer getScreenHeight() {
        return screenHeight;
    }

    public static void setScreenHeight(Integer screenHeight) {
        Constants.screenHeight = screenHeight;
    }
}
