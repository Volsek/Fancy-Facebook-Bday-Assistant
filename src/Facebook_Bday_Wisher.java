import com.google.common.escape.UnicodeEscaper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public class Facebook_Bday_Wisher {

    public static void main(String[] args) throws IOException {
        String email = null;
        String password = null;
        String Xpath;
        WebDriverManager.chromedriver().setup();
        //Load config file or set it up if not already created
        Log("Checking Config File");
        Properties settings = new Properties();
        try {
            InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "\\settings.properties");
            settings.load(inputStream);
            email = settings.getProperty("Email");
            password = settings.getProperty("Password");
        } catch (IOException e) {
            OutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\settings.properties");
            Scanner scanner = new Scanner(System.in);
            Log("Email:");
            settings.setProperty("Email", scanner.next());
            Log("Password:");
            settings.setProperty("Password", scanner.next());
            settings.store(outputStream, null);
            email = settings.getProperty("Email");
            password = settings.getProperty("Password");
        }


        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.addArguments("--incognito");
        //options.addArguments("--headless"); todo enable for final product
        ChromeDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.ignoring(NoSuchElementException.class);

        //Go to facebook and login
        driver.get("https://www.facebook.com");
        Xpath = new String("//input[@type=\"submit\"]");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath)));
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("pass")).sendKeys(password);
        driver.findElement(By.id("u_0_b")).click();

        //Wait for page to load and go to Birthdays
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bluebarRoot")));
        driver.navigate().to("https://www.facebook.com/events/birthdays/");

        //Check if there are any Birthdays today
        if (driver.findElements(By.id("birthdays_today_card")).size() != 0) {
            var Classname = driver.findElement(By.id("birthdays_today_card")).getAttribute("class");
            int spacePos = Classname.indexOf(" ");
            if (spacePos > 0) {
                Classname = Classname.substring(0, spacePos);
            }
            Xpath = new String("(//div[@id=\"birthdays_content\"]//div[@class=\"" + Classname + "\"])[1]//child::textarea");
            if (driver.findElementsByXPath(Xpath).size() > 0) {
                Xpath = new String("(//div[@id=\"birthdays_content\"]//div[@class=\"" + Classname + "\"])[1]//child::a[@title]");
                var overallclass = driver.findElementsByXPath(Xpath);
                List<String> names = new ArrayList<>();
                for (var anchor : overallclass) {
                    names.add(anchor.getAttribute("title"));
                }
                //todo remove logging
                for (String name :
                        names) {
                    Log(name);
                }
                //Post to facebook
                Xpath = new String("(//div[@id=\"birthdays_content\"]//div[@class=\"" + Classname + "\"])[1]//child::textarea");
                var alltextboxes = driver.findElementsByXPath(Xpath);
                for (int i = 0; i < alltextboxes.size(); i++) {
                    spacePos = names.get(i).indexOf(" ");
                    var first_name = names.get(i).substring(0, spacePos);
                    alltextboxes.get(i).sendKeys("Happy Birthday " + first_name + "!" + Keys.ENTER);
                }
            }


        } else {
            Log("No Birthdays Found today");
        }

    }

    private static void Log(String msg) {
        System.out.println(msg);
    }
}
