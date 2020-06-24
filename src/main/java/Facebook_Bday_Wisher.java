import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.text.StringEscapeUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;


public class Facebook_Bday_Wisher {

    static String email;
    static String password;

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            //Load config file or set it up if not already created
            ConfigSetup(args);

            //Setup Chrome Driver
            var driver = DriverSetup();


            //Go to facebook,login and head to Birthdays
            Facebook_Login(driver);


            //Check if there are any Birthdays today
            Check_For_Birthdays(driver);


            driver.quit(); //todo check if wait is needed to send before quit

            System.exit(1);
        }catch (Exception e){
            main(args);
        }



    }

    private static void Log(String msg) {
        System.out.println(msg);
    }


    private static void ConfigSetup(String[] args) throws IOException {
        Log("Checking Config File");
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("$dUFy+jf2L2wZ7&m");
        Properties settings = new EncryptableProperties(encryptor);
        try {
            InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "\\settings.properties");
            settings.load(inputStream);
            email = encryptor.decrypt(settings.getProperty("Email"));
            password = encryptor.decrypt(settings.getProperty("Password"));
        } catch (IOException e) {
            GUI.main(args);
            InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "\\settings.properties");
            settings.load(inputStream);
            email = encryptor.decrypt(settings.getProperty("Email"));
            password = encryptor.decrypt(settings.getProperty("Password"));
        }


    }

    private static ChromeDriver DriverSetup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.addArguments("--incognito");
        options.addArguments("--headless"); //todo enable for final product
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

    private static void Check_For_Birthdays(ChromeDriver driver) throws InterruptedException {
        String Xpath;
        //Gets today's class name, they change everyday and the first sequence of today's card corresponds to today's birthday card
        try {
            var Classname = driver.findElement(By.id("birthdays_today_card")).getAttribute("class");
            int spacePos = Classname.indexOf(" ");
            if (spacePos > 0) {
                Classname = Classname.substring(0, spacePos);
            }

            Xpath = StringEscapeUtils.escapeJava("(//div[@id='birthdays_content']//div[@class='" + Classname + "'])[1]//child::textarea");

            //Perform check to see if there are any Birthdays today and if so then gather the profiles
            //I.e Names and Text boxes to send birthday wishes

            if (driver.findElement(By.id("birthdays_today_card")) != null && driver.findElementsByXPath(Xpath).size() > 0) {
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

                Post_Wishes_to_Facebook(BirthdayPeople_List, driver);
            } else {
                Log("All birthday messages have been sent");
            }
        } catch (NoSuchElementException elementException) {
            Log("No Birthdays Found today");

        }
    }

    private static void Post_Wishes_to_Facebook(ArrayList<Birthday_Person> Birthday_List, ChromeDriver driver) throws InterruptedException {
        //Post to facebook
        ArrayList Polish_Names = new ArrayList<String>(Arrays.asList("Jan", "Maks", "Stanisław", "Andrzej", "Józef", "Tadeusz", "Jerzy", "Zbigniew", "Krzysztof", "Henryk", "Ryszard", "Kazimierz", "Marek", "Marian", "Piotr", "Janusz", "Władysław", "Adam", "Wiesław", "Zdzisław", "Edward", "Mieczysław", "Roman", "Mirosław", "Grzegorz", "Czesław", "Dariusz", "Wojciech", "Jacek", "Eugeniusz", "Tomasz", "Stefan", "Zygmunt", "Leszek", "Bogdan", "Antoni", "Paweł", "Franciszek", "Sławomir", "Waldemar", "Jarosław", "Robert", "Mariusz", "Włodzimierz", "Michał", "Zenon", "Bogusław", "Witold", "Aleksander", "Bronisław", "Wacław", "Bolesław", "Ireneusz", "Maciej", "Artur", "Edmund", "Marcin", "Lech", "Karol", "Rafał", "Arkadiusz", "Leon", "Sylwester", "Lucjan", "Julian", "Wiktor", "Romuald", "Bernard", "Ludwik", "Feliks", "Alfred", "Alojzy", "Przemysław", "Cezary", "Daniel", "Mikołaj", "Ignacy", "Lesław", "Radosław", "Konrad", "Bogumił", "Szczepan", "Gerard", "Hieronim", "Krystian", "Leonard", "Wincenty", "Benedykt", "Hubert", "Sebastian", "Norbert", "Adolf", "Łukasz", "Emil", "Teodor", "Rudolf", "Joachim", "Jakub", "Walenty", "Alfons", "Damian", "Rajmund", "Szymon", "Zygfryd", "Leopold", "Remigiusz", "Florian", "Konstanty", "Augustyn", "Albin", "Bohdan", "Dominik", "Gabriel", "Teofil", "Brunon", "Juliusz", "Walerian", "Bartłomiej", "Fryderyk", "Eryk", "Anatol", "Maksymilian", "Gustaw", "Aleksy", "Longin", "Bartosz", "Wilhelm", "Ferdynand", "Erwin", "Klemens", "Lechosław", "Ernest", "Seweryn", "Herbert", "Albert", "Błażej", "Izydor", "Dionizy", "Edwin", "Ginter", "Adrian", "Mateusz", "Walter", "Helmut", "Bazyli", "Marceli", "Sergiusz", "Bonifacy", "Werner", "Eligiusz", "Wawrzyniec", "Kamil", "Łucjan", "Olgierd", "Arnold", "Jacenty", "Dawid", "Ewald", "Manfred", "Emilian", "Klaudiusz", "Zbysław", "Igor", "Benon", "Jędrzej", "Wit", "Hilary", "Apolinary", "Fabian", "Zenobiusz", "Horst", "Gerhard", "Roland", "Euzebiusz", "Hipolit", "Filip", "Nikodem", "Miron", "January", "Kajetan", "Bazyl", "Emanuel", "Idzi", "Donat", "August", "Dymitr", "Ksawery", "Ludomir", "Narcyz", "Lubomir", "Witalis", "Roch", "Miłosz", "Telesfor", "Heronim", "Ziemowit", "Borys", "Oskar", "Zbyszko", "Krystyn", "Zbyszek", "Cyryl", "Gracjan", "Patryk", "Reinhold", "Eliasz", "Ewaryst", "Felicjan", "Rufin", "Bruno", "Herman", "Beniamin", "Kryspin", "Rajnold", "Apoloniusz", "Engelbert", "Cyprian", "Walery", "Medard", "Gwidon", "Celestyn", "Jaromir", "Tytus", "Wiaczesław", "Kornel", "Wieńczysław", "Maurycy", "Oswald", "Jeremi", "Kurt", "Ingrid", "Klaus", "Damazy", "Eustachy", "Otton", "Korneliusz", "Cezariusz", "Tymoteusz", "Justyn", "Otto", "Janisław", "Anastazy", "Ambroży", "Polikarp", "Heliodor", "Jurek", "Saturnin", "Dieter", "Winicjusz", "Wolfgang", "Gotfryd", "Modest", "Margot", "Sylweriusz", "Marcel", "Radzisław", "Bogusz", "Witosław", "Leonid", "Serafin", "Reinhard", "Diter", "Dyonizy", "Wenancjusz", "Olaf", "Wasyl", "Anatoliusz", "Januariusz", "Kacper", "Oleg", "Rościsław", "Sławoj", "Erazm", "Dobiesław", "Jurand", "Karin", "Aureliusz", "Wilibald", "Heinz", "Rajnard", "Dobrosław", "Erhard", "Radomir", "Egon", "Harald", "Eustachiusz", "Kordian", "Napoleon", "Roger", "Onufry", "Wendelin", "Włodzisław", "Eugieniusz", "Wirgiliusz", "Jeremiasz", "Anzelm", "Ruth", "Lucjusz", "Anatoli", "Inez", "Iwo", "Maria", "Krystyna", "Anna", "Barbara", "Teresa", "Elżbieta", "Janina", "Zofia", "Jadwiga", "Danuta", "Halina", "Irena", "Ewa", "Małgorzata", "Helena", "Grażyna", "Bożena", "Stanisława", "Jolanta", "Marianna", "Urszula", "Wanda", "Alicja", "Dorota", "Agnieszka", "Beata", "Katarzyna", "Joanna", "Wiesława", "Renata", "Iwona", "Genowefa", "Kazimiera", "Stefania", "Hanna", "Lucyna", "Józefa", "Alina", "Mirosława", "Aleksandra", "Władysława", "Henryka", "Czesława", "Lidia", "Regina", "Monika", "Magdalena", "Bogumiła", "Marta", "Marzena", "Leokadia", "Mariola", "Bronisława", "Aniela", "Bogusława", "Eugenia", "Izabela", "Cecylia", "Emilia", "Łucja", "Gabriela", "Sabina", "Zdzisława", "Agata", "Edyta", "Aneta", "Daniela", "Wioletta", "Sylwia", "Weronika", "Antonina", "Justyna", "Gertruda", "Mieczysława", "Franciszka", "Rozalia", "Zuzanna", "Natalia", "Celina", "Ilona", "Alfreda", "Wiktoria", "Olga", "Wacława", "Róża", "Karolina", "Bernadeta", "Julia", "Michalina", "Adela", "Ludwika", "Honorata", "Aldona", "Eleonora", "Violetta", "Felicja", "Walentyna", "Pelagia", "Apolonia", "Brygida", "Zenona", "Izabella", "Romana", "Zenobia", "Waleria", "Anita", "Bożenna", "Romualda", "Marzanna", "Anastazja", "Kamila", "Aurelia", "Ewelina", "Ludmiła", "Hildegarda", "Teodozja", "Feliksa", "Nina", "Paulina", "Longina", "Julianna", "Klara", "Marlena", "Teodora", "Leonarda", "Ryszarda", "Liliana", "Kinga", "Lilianna", "Albina", "Elwira", "Gizela", "Dariusz", "Bolesława", "Otylia", "Karina", "Arleta", "Marzenna", "Melania", "Kornelia", "Salomea", "Adelajda", "Eryka", "Dominika", "Sławomira", "Donata", "Eliza", "Tamara", "Zyta", "Bernadetta", "Nadzieja", "Bernarda", "Irmina", "Julita", "Wiera", "Dagmara", "Wioleta", "Matylda", "Edwarda", "Lilla", "Klaudia", "Żaneta", "Tatiana", "Elfryda", "Patrycja", "Anetta", "Lilia", "Teofila", "Daria", "Maryla", "Rita", "Amelia", "Eulalia", "Lila", "Lucja", "Leontyna", "Luba", "Kunegunda", "Ruta", "Sonia", "Seweryna", "Jarosława", "Klementyna", "Adriana", "Edeltrauda", "Filomena", "Angelika", "Tekla", "Blandyna", "Florentyna", "Luiza", "Gerda", "Krzysztofa", "Adrianna", "Martyna", "Inga", "Balbina", "Erna", "Domicela", "Zinaida", "Bogna", "Helga", "Lubomira", "Bernardyna", "Maja", "Kamilla", "Benedykta", "Ligia", "Irmgarda", "Leonia", "Olimpia", "Bogdana", "Amalia", "Eufemia", "Hanka", "Mirella", "Laura", "Milena", "Raisa", "Ludomira", "Petronela", "Wilhelmina", "Konstancja", "Mirela", "Wincentyna", "Marcela", "Ingeborga", "Benigna", "Zenaida", "Hieronima", "Dobrosława", "Sylwestra", "Augustyna", "Erika", "Prakseda", "Lena", "Irma", "Berta", "Scholastyka", "Roma", "Marcelina", "Blanka", "Ernestyna", "Judyta", "Magda", "Andżelika", "Wirginia", "Estera", "Malwina", "Ala", "Narcyza", "Hermina", "Cyryla", "Kalina", "Adamina", "Celestyna", "Arletta", "Jowita", "Iwonna", "Larysa", "Lesława", "Alojza", "Dioniza", "Diana", "Stella", "Ida", "Marcjanna", "Modesta", "Aniceta", "Serafina", "Adolfina", "Fryderyka", "Ksenia", "Emma", "Ksawera", "Sława", "Wielisława", "Otolia", "Włodzimiera", "Lucjanna", "Gabryela", "Wincenta", "Iza", "Izydora", "Kornela", "Radosława", "Heronima", "Natasza", "Adolfa", "Galina", "Julitta", "Bernardeta", "Ada", "Bibianna", "Leona", "Edmunda", "Jagoda", "Elza", "Ludwina", "Józefina", "Zbigniewa"));
        int spacePos;
        for (var person : Birthday_List) {
            spacePos = person.Name.indexOf(" ");
            int dashPos = person.Name.indexOf("-");
            String first_name;
            if (dashPos != -1 && spacePos > dashPos) {
                first_name = person.Name.substring(0, dashPos);
            } else {
                first_name = person.Name.substring(0, spacePos);
            }
            if (Polish_Names.contains(first_name)) {
                person.Textbox.sendKeys("Wszystkiego Najlepszego " + first_name + "! ");
            } else {
                person.Textbox.sendKeys("Happy Birthday " + first_name + "! ");
            }
            var JS_ADD_TEXT_TO_INPUT = " var elm = arguments[0], txt = arguments[1]; elm.value += txt; elm.dispatchEvent(new Event('change')); ";
            var elem = person.Textbox;
            var text = "\uD83C\uDF89";
            for (int i = 0; i < 2; i++) {
                driver.executeScript(JS_ADD_TEXT_TO_INPUT, elem, text);
            }
            person.Textbox.sendKeys(Keys.ENTER);
            boolean wait = true;
            while(wait){
                try {
                    wait = person.Textbox.isDisplayed();
                }catch (StaleElementReferenceException e){
                    break;
                }
                Thread.sleep(1000);
            }



        }
        Log(Birthday_List.size() + " birthdays have been wished");
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
