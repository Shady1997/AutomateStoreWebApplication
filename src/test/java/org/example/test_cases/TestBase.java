package org.example.test_cases;

import java.awt.AWTException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import org.example.pages.PageBase;

public class TestBase {
	// npm install -g newman - newman run <collection name> --data <file name> -n
	// <no of iterations> -d <delay time> -e <environment name>

	// define main properties
	public static WebDriver driver;
	FileInputStream readProperty;
	public static Properties prop;
	public static ChromeOptions options;
	JavascriptExecutor js;
	static ExtentTest test;
	static ExtentReports report;

	@Parameters("browser")
	@BeforeTest
	public void prepareClassProperties(String browser) throws IOException, AWTException {
		// define extent report objects
//		report = new ExtentReports();
//		test = report.createTest("ExtentDemo");

		readProperty = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\generalProperties.properties");
		prop = new Properties();
		prop.load(readProperty);
		options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-web-security");
		options.addArguments("--no-proxy-server");

		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("credentials_enable_service", false);
		prefs.put("profile.password_manager_enabled", false);

		options.setExperimentalOption("prefs", prefs);
		options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });

		if (browser.equalsIgnoreCase("Firefox")) {
			System.setProperty("webdriver.gecko.driver",
					System.getProperty("user.dir") + prop.getProperty("firefoxdriver"));
			driver = new FirefoxDriver();
		} else if (browser.equalsIgnoreCase("Chrome")) {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + prop.getProperty("chromedriver"));
			driver = new ChromeDriver(options);
		} else {
			throw new IllegalArgumentException("Invalid browser value!!");
			// Change thread count 1 for sequential , 2 for parallel 3 ..browser..
		}

		js = (JavascriptExecutor) driver;
		// Set Driver wait
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@Test(priority = 1, groups = "smoke", description = "Start MyStore Web Application")
	private void startApplication() throws InterruptedException {
		// Maximize current window
		driver.manage().window().maximize();
		// navigate to website
		driver.get("http://automationpractice.com/index.php");
		// take screenshot for home page
		PageBase.captureScreenshot(driver, "HomePage");
	}

	@AfterTest
	public void tearDown() {
		driver.quit();
	}

	public static void getScreenshotOnFailure() {
		PageBase.captureScreenshot(driver, "fail" + java.time.LocalTime.now().toString());
	}

}
