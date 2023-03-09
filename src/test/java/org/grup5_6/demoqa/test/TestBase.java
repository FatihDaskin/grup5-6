package org.grup5_6.demoqa.test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.grup5_6.demoqa.utilities.BrowserUtils;
import org.grup5_6.demoqa.utilities.ConfigurationReader;
import org.grup5_6.demoqa.utilities.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestBase {
    protected WebDriver driver;
    protected Actions actions;
    protected WebDriverWait wait;

    protected ExtentReports report;
    protected ExtentHtmlReporter htmlReporter;
    protected ExtentTest extentLogger;

    @BeforeTest
    public void setUpTest(){

        //This will initialize the ExtentReports class
        report = new ExtentReports();

        //Create a report path --> how can we find our project dynamically
        String projectPath= System.getProperty("user.dir");
//        String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//        String path= projectPath + "/test-output/report"+date+".html";
        String path= projectPath + "/test-output/report.html";

        //initialize HTML report with the report path
        htmlReporter = new ExtentHtmlReporter(path);

        //Attach the HTML report to the report object
        report.attachReporter(htmlReporter);

        //We need to give a title to report
        htmlReporter.config().setReportName("Audit Smoke Test");

        //Set environment information --> Test Name, Tester Name, Browser, Test Steps (Admin/User), Test Data, Date and Time, Operating System, Result
        report.setSystemInfo("Environment","Production");
        report.setSystemInfo("Browser", ConfigurationReader.get("browser"));
        report.setSystemInfo("OS", System.getProperty("os.name"));
        report.setSystemInfo("Test Engineer","Ihsan");

    }

    @AfterTest
    public void tearDownTest(){
        report.flush();
    }



    @BeforeMethod
    public void setUp() {
        driver= Driver.get();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        actions= new Actions(driver);
        wait=new WebDriverWait(Driver.get(),10);
        driver.get(ConfigurationReader.get("url"));

    }

    @AfterMethod
    public void tearDown(ITestResult result) throws InterruptedException, IOException {

        // create a condition--> If test is FAILED
        if (result.getStatus()==ITestResult.FAILURE){//if it is failed
            //Record name of test
            extentLogger.fail(result.getName());

            //Take the screenshot and return its location
            String screenshotPath= BrowserUtils.getScreenshot(result.getName());
            //Add the screenshot to the report
            extentLogger.addScreenCaptureFromPath(screenshotPath);
            //Capture the exception and put into report
            extentLogger.fail(result.getThrowable());

        }


        Thread.sleep(3000);
        // driver.close(); // BECAUSE OF DRIVER UTILITY CLASS WE DO NOT NEED ANYMORE
        Driver.closeDriver();
    }
}
