import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

public class MainRun {

  public int numberOfChannels = 19, channelIterator;
  String channelName, channelLocation;
  WebDriver driver;

  @Test(testName = "Create M3U Playlist")
  public void createPlaylist() throws Exception {
    openWebPage();
    IPTVCatPage page = new IPTVCatPage(driver);

    for (channelIterator = 0; channelIterator <= numberOfChannels; channelIterator++) {
      excelDataReader();
      System.out.println(channelName);
      System.out.println(channelLocation);
      page.getSearchInput().sendKeys(channelName);
      page.getSubmitSearchButton().click();
      driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.SECONDS);

      //Look through the table of results
      int rowCount = driver.findElements(By.xpath("//tbody [@class='streams_table']")).size();
    }
  }

  public void openWebPage(){
    System.setProperty("webdriver.chrome.driver", "C:\\ITB-AnkrPT\\CatIPTVM3u\\src\\chromedriver1.exe");
    driver = new ChromeDriver();
    driver.get(Utils.BASE_URL);
    driver.manage().window().maximize();
  }

  public void excelDataReader() throws Exception {
    File src = new File("C:\\ITB-AnkrPT\\CatIPTVM3u\\src\\channels.xlsx");
    FileInputStream fis = new FileInputStream(src);
    XSSFWorkbook xsf = new XSSFWorkbook(fis);
    XSSFSheet sheet = xsf.getSheet("Sheet1");
    channelName = sheet.getRow(channelIterator).getCell(0).getStringCellValue();
    channelLocation = sheet.getRow(channelIterator).getCell(1).getStringCellValue();
  }
}