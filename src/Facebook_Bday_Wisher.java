import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;


public class Facebook_Bday_Wisher {

    static String email;
    static String password;

    public static void main(String[] args) throws IOException {
        //Load config file or set it up if not already created

        ConfigSetup();

        //Setup Chrome Driver
        var driver = DriverSetup();


        //Go to facebook,login and head to Birthdays
        Facebook_Login(driver);


        //Check if there are any Birthdays today
        Check_For_Birthdays(driver);


        //driver.quit();


    }

    private static void Log(String msg) {
        System.out.println(msg);
    }

    private static void ConfigSetup() throws IOException {
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
    }

    private static ChromeDriver DriverSetup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.addArguments("--incognito");
        //options.addArguments("--headless"); todo enable for final product
        return new ChromeDriver(options);
    }

    private static void Facebook_Login(ChromeDriver driver) {
        String Xpath;
        driver.get("https://www.facebook.com");
        Xpath = StringEscapeUtils.escapeJava("//input[@type='submit']");
        driver_wait(driver).until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath)));
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("pass")).sendKeys(password);
        driver.findElement(By.id("u_0_b")).click();

        //Wait for page to load and go to Birthdays
        driver_wait(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("bluebarRoot")));
        driver.navigate().to("https://www.facebook.com/events/birthdays/");
    }

    private static WebDriverWait driver_wait(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.ignoring(NoSuchElementException.class);
        return wait;
    }

    private static void Check_For_Birthdays(ChromeDriver driver) {
        String Xpath;
        //Gets today's class name, they change everyday and the first sequence of today's card corresponds to today's birthday card
        var Classname = driver.findElement(By.id("birthdays_today_card")).getAttribute("class");
        int spacePos = Classname.indexOf(" ");
        if (spacePos > 0) {
            Classname = Classname.substring(0, spacePos);
        }
        Xpath = StringEscapeUtils.escapeJava("(//div[@id='birthdays_content']//div[@class='" + Classname + "'])[1]//child::textarea");

        //Perform check to see if there are any Birthdays today and if so then gather the profiles
        //I.e Names and Text boxes to send birthday wishes
        if (driver.findElements(By.id("birthdays_today_card")).size() != 0 && driver.findElementsByXPath(Xpath).size() > 0) {
            Xpath = StringEscapeUtils.escapeJava("(//div[@id='birthdays_content']//div[@class='" + Classname + "'])[1]//child::li[@class]");

            var BirthdayPeople_List = new ArrayList();
            var li_list = driver.findElementsByXPath(Xpath);

            for (var item : li_list) {
                Xpath = StringEscapeUtils.escapeJava(".//textarea");
                var Textbox = item.findElements(By.xpath(Xpath));
                if (!Textbox.isEmpty()) {
                    Xpath = StringEscapeUtils.escapeJava(".//a[@title]");
                    String Name = item.findElement(By.xpath(Xpath)).getAttribute("title");
                    Birthday_Person tmp = new Birthday_Person(Name, Textbox.get(0));
                    BirthdayPeople_List.add(tmp);
                }
            }

                Post_Wishes_to_Facebook(BirthdayPeople_List);
        } else {
            Log("No Birthdays Found today");
        }
    }

    private static void Post_Wishes_to_Facebook( ArrayList<Birthday_Person> Birthday_List) {
        //Post to facebook
        int spacePos;
        for (var person : Birthday_List) {
            spacePos = person.Name.indexOf(" ");
            var first_name = person.Name.substring(0, spacePos);
            person.Textbox.sendKeys("Happy Birthday " + first_name + "!"); //+ Keys.ENTER); todo add back the confirm after testing
        }
    }

    static class Birthday_Person {
        String Name;
        WebElement Textbox;

        Birthday_Person(String name, WebElement textbox) {
            Name = name;
            Textbox = textbox;
        }
    }
}
