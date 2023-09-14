package src;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ShoppingCartValidation {

	private WebDriver driver;
	private final String baseUrl = "https://devwcs2.frontgate.com/ShoppingCartView";
	private final String excelFilePath = "C:\\Users\\user\\Downloads\\book1.xlsx";

	// Helper method to check if an element is present
	private boolean isElementPresent(By locator) {
		try {
			driver.findElement(locator);
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		}
	}

	@BeforeTest
	public void setup() {
		// Set the path to the ChromeDriver executable
		System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver.exe");

		// Initialize ChromeDriver
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--ignore-certificate-errors");
		driver = new ChromeDriver(options);

		// Maximize the browser window
		driver.manage().window().maximize();
	}

	@Test
	public void testLoginWithMultipleUsers() throws IOException, InterruptedException {
		// Load the Excel file
		FileInputStream inputStream = new FileInputStream(excelFilePath);
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheet("User Data");

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			String email = row.getCell(0).getStringCellValue();
			String password = row.getCell(1).getStringCellValue();

			// Open the shopping cart page
			driver.get(baseUrl);

			// Find and fill the username and password fields
			WebElement emailField = driver.findElement(By.cssSelector("#gwt-sign-in-modal"));
			WebElement passwordField = driver.findElement(By.cssSelector("#passwordReset"));
			WebElement loginButton = driver.findElement(By.id("logonButton"));

			emailField.sendKeys(email);
			passwordField.sendKeys(password);
    		Thread.sleep(1000);
			loginButton.click();
			
			// Check for the presence of error message elements
			boolean hasInvalidEmailError = isElementPresent(By.cssSelector("#error-div-gwt-sign-in-modal"));
			boolean hasEmptyPasswordError = isElementPresent(By.cssSelector("#error-div-passwordReset"));
			boolean hasWrongPasswordError = isElementPresent(By.cssSelector(
					"#shopping-cart-v2-root > div > div.main-panel > div.left-main-panel > div.empty-cart-sign-in-container > div:nth-child(2) > div > div.signin-input-holder > div.error-panel > div"));

			// Check for any error message displayed
			if (!hasInvalidEmailError && !hasEmptyPasswordError && !hasWrongPasswordError) {
				System.out.println("Success login for email: " + email + " and password: " + password);
			} else {
				System.out.println("Failed login for email: " + email + " and password: " + password);
			}
		}

		// Close the workbook and input stream
		workbook.close();
		inputStream.close();
	}

	@AfterTest
	public void tearDown() {
		// Close the browser after the test
		driver.quit();
	}

}