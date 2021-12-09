import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainRun {

  int numberOfChannels, channelIterator, resultsIterator, newIteratorValue;
  String channelName, channelLocation, channelFlagCheck, channelActivityCheck;
  WebDriver driver;

  @Test
  public void createPlaylist() throws Exception {
    openWebPage();

    for (channelIterator = 1; channelIterator <= numberOfChannels - 1; channelIterator++) {
      excelDataReader();

      IPTVCatPage page = new IPTVCatPage(driver);
      //Clears the channel search input box
      if (channelIterator != 1) {
        page.getSearchInput().clear();
      }
      System.out.println(channelName);
      driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
      page.getSearchInput().sendKeys(channelName);
      driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
      page.getSubmitSearchButton().click();
      driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

      //Check to make sure the result is empty
      if (!driver.getPageSource().contains("Nothing found!")) {

        //Look through the table of results
        WebElement table = driver.findElement(By.xpath("//tbody [@class='streams_table']"));
        List<WebElement> rows = table.findElements(By.xpath("//tr[contains(@class, 'border-solid belongs_to')]"));

        //Checks the amount of results and trawls for active links
        for (resultsIterator = 1; resultsIterator <= rows.size(); resultsIterator++) {
          channelFlagCheck = driver.findElement(By.xpath("//tr[contains(@class, 'border-solid belongs_to')] [" + resultsIterator + "]" + " //td [@class = 'flag'] //a")).getAttribute("href").substring(20).replace("_", " ").replace("-", "").trim();
          channelActivityCheck = driver.findElement(By.xpath("//tr[contains(@class, 'border-solid belongs_to')] [" + resultsIterator + "]" + " //td /child::div [contains(@class, 'state')]")).getAttribute("class").replace("state ", "");
          if (channelFlagCheck.contains(channelLocation) && channelActivityCheck.equals("online")) {
            addToList();
          } else {
            System.out.println("Inactive link found for " + channelName);
          }
        }
      } else
        System.out.println("No streams were found for: " + channelName);
    }
    downloadTheFile();
  }

  public void openWebPage() throws Exception {
    setChromeOptions();
    driver.get(Utils.BASE_URL);
    driver.manage().window().maximize();
    new IPTVCatPage(driver).getAcceptCookies().click();
    File src = new File("src/channels.xlsx");
    FileInputStream fis = new FileInputStream(src);
    XSSFWorkbook xsf = new XSSFWorkbook(fis);
    XSSFSheet sheet = xsf.getSheet("ChannelList");
    numberOfChannels = sheet.getPhysicalNumberOfRows();
  }

  public void setChromeOptions() {
    System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
    String downloadFilepath = "E:\\IPTV\\CatIPTVM3u\\src\\M3uDownloads";
    HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
    chromePrefs.put("profile.default_content_settings.popups", 0);
    chromePrefs.put("download.default_directory", downloadFilepath);
    ChromeOptions options = new ChromeOptions();
    options.setExperimentalOption("prefs", chromePrefs);
    DesiredCapabilities cap = DesiredCapabilities.chrome();
    cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    cap.setCapability(ChromeOptions.CAPABILITY, options);
    driver = new ChromeDriver(cap);
  }

  public void excelDataReader() throws Exception {
    //Finds the channel name & location to search
    File src = new File("src/channels.xlsx");
    FileInputStream fis = new FileInputStream(src);
    XSSFWorkbook xsf = new XSSFWorkbook(fis);
    XSSFSheet sheet = xsf.getSheet("ChannelList");
    channelName = sheet.getRow(channelIterator).getCell(0).getStringCellValue();
    channelLocation = sheet.getRow(channelIterator).getCell(1).getStringCellValue();
  }

  public void addToList() throws Exception {
    //Adds the active links to the downloadable file in IPTVCat
    newIteratorValue = resultsIterator * 2;
    if (newIteratorValue >= 20) {
      newIteratorValue = newIteratorValue + 1;
    }
    var addToListButton = driver.findElement(By.xpath("//tr [" + newIteratorValue + "] //td [@colspan = '7'] //tr //td[1]"));
    if (!addToListButton.isDisplayed()) {
      throw new Exception("Add to list button failed to load");
    }
    Actions actions = new Actions(driver);
    Thread.sleep(350);
    actions.moveToElement(addToListButton).click().perform();
    Thread.sleep(350);
    driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
  }

  public void downloadTheFile() throws Exception {
    clearOriginalFilePriorToDownload();
    var href = new IPTVCatPage(driver).getDownloadM3uFile().getAttribute("href");
    driver.get(href);
    File m3uFile = new File("src/M3uDownloads/my_list.iptvcat.com.m3u8");
    if (
        m3uFile.canExecute()) {
      System.out.println("Should be updated!");
    } else System.out.println("Should be updated");
  }

  public void clearOriginalFilePriorToDownload() throws Exception {
    File m3uFile = new File("src/M3uDownloads/my_list.iptvcat.com.m3u8");
    if (m3uFile.exists()) {
      if (m3uFile.delete()) {
        System.out.println("The file has been deleted prior to addition of new file");
      } else throw new Exception("Unable to delete file, please check that the file exists prior to deletion");
    }
    System.out.println("M3u file doesn't exist, no need to delete");
  }
}