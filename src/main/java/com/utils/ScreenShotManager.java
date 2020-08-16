package com.utils;

import com.ApplicationManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;

public class ScreenShotManager {

    public static void saveScreenShot() {
        File screenShot = ((TakesScreenshot) ApplicationManager.getDriver()).getScreenshotAs(OutputType.FILE);
        try {
            String current = new File(".").getCanonicalPath();
            System.out.println("Current dir:" + current);
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current dir using System:" + currentDir);
            FileUtils.copyFile(screenShot, new File(current + "/test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
