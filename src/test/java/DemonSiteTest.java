import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DemonSiteTest {
    private static ExtentReports report;
    private WebDriver webDriver;
    private static final String BASE_URL = "http://thedemosite.co.uk/";
    SpreadSheetReader spreadReader;

    @BeforeClass
    public static void init() {
        report = new ExtentReports();
        String fileName = "MyReport" + ".html";
        String filePath = System.getProperty("user.dir")
                + File.separatorChar + fileName;
        report.attachReporter(new ExtentHtmlReporter(filePath));
    }


    @Before
    public void setUp() {
        webDriver = new ChromeDriver();
        webDriver.navigate().to(BASE_URL);
        spreadReader = new SpreadSheetReader(System.getProperty("user.dir")+"\\src\\main\\resources\\Example_Spreadsheet.xlsx");
    }

    @Test
    public void printTest() {
        String path = "";
        Page page = PageFactory.initElements(webDriver, Page.class);
        NewUserPage newUserPage = PageFactory.initElements(webDriver, NewUserPage.class);
        LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
        String name = "";
        String pass = "";

        for(int i = 1; i<=(spreadReader.getLast());i++) {
            List<String> row = spreadReader.readRow(i, "Input Data");
            name = row.get(2);
            pass = row.get(3);
            System.out.println(name + pass);

            ExtentTest test = report.createTest("MyFirstTest");
            test.log(Status.INFO, "My First Test is Starting ");
            page.HomePage();
            test.log(Status.DEBUG, "Home Page");

            newUserPage.createUser(name, pass);
            test.log(Status.DEBUG, "newUser Page");
            loginPage.LoggingIn(name, pass);
            test.log(Status.DEBUG, "Login Page");

        /*    try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            try {
                Assert.assertEquals("Login Failed", "**Successful Login**", loginPage.getLogin.getText());
                path = ScreenShot.take(webDriver, "Passed");
                //test.addScreenCaptureFromPath(path);
                test.addScreenCaptureFromPath(path, "WHATS THE POINT IN THIS");
                test.pass("Passed");
            } catch (AssertionError e) {
                test.fail("Failed");
                try {
                    path = ScreenShot.take(webDriver, "Failed");
                    test.addScreenCaptureFromPath(path);
                } catch (IOException e2) {
                }
                throw e;
            } catch (IOException e3) {
            }
        }
    }

    @After
    public void tidy() {
        webDriver.quit();
    }

    @AfterClass
    public static void cleanUp() {
        report.flush();
    }
}