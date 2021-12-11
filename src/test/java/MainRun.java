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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainRun {

  int numberOfChannels, channelIterator, resultsIterator, newIteratorValue, rows, channelLiveDaysCheckAsInt, channelLivelinessCheckAsInt;
  String channelName, channelLocation, channelFlagCheck, channelActivityCheck, channelLivelinessCheck, channelLiveDaysCheck, link, fileUsername, dailyIPTVFilename;
  WebDriver driver;
  boolean iptvFlag = true;
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_RED = "\u001B[31m";

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
      System.out.println(channelName + ":");
      driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
      page.getSearchInput().sendKeys(channelName);
      driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
      page.getSubmitSearchButton().click();
      driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

      //Check to see if the result is empty
      if (!driver.getPageSource().contains("Nothing found!")) {

        //Look through the table of results
        WebElement table = driver.findElement(By.xpath("//tbody [@class='streams_table']"));
        List<WebElement> row = table.findElements(By.xpath("//tr[contains(@class, 'border-solid belongs_to')]"));
        rows = row.size();
        if (rows >= 20) {
          rows = 20;
        }

        //Checks the amount of results and trawls for active links
        for (resultsIterator = 1; resultsIterator <= rows; resultsIterator++) {
          channelFlagCheck = driver.findElement(By.xpath("//tr[contains(@class, 'border-solid belongs_to')] [" + resultsIterator + "]" + " //td [@class = 'flag'] //a")).getAttribute("href").substring(20).replace("_", " ").replace("-", "").trim();
          channelActivityCheck = driver.findElement(By.xpath("//tr[contains(@class, 'border-solid belongs_to')] [" + resultsIterator + "]" + " //td /child::div [contains(@class, 'state')]")).getAttribute("class").replace("state ", "");
          channelLivelinessCheck = driver.findElement(By.xpath("//tr[contains(@class, 'border-solid belongs_to')] [" + resultsIterator + "]" + " //td /child::div [contains(@class, 'live')] //div")).getText();
          channelLiveDaysCheck = driver.findElement(By.xpath("//tr[contains(@class, 'border-solid belongs_to')] [" + resultsIterator + "]" + " //td /child::div [contains(@class, 'mature')]")).getText();
          channelLivelinessCheckAsInt = Integer.parseInt(channelLivelinessCheck);
          channelLiveDaysCheckAsInt = Integer.parseInt(channelLiveDaysCheck);

          if ((rows <= 7 && channelFlagCheck.contains(channelLocation) && channelActivityCheck.equals("online") && channelLivelinessCheckAsInt >= 70) || (rows >= 8 && channelFlagCheck.contains(channelLocation) && channelActivityCheck.equals("online") && channelLivelinessCheckAsInt >= 85)) {
            addToList();
            System.out.println(ANSI_GREEN + "Active link found for " + channelName + ANSI_RESET);
          } else {
            System.out.println(ANSI_RED + "Inactive link found for " + channelName + ANSI_RESET);
          }
        }
      } else
        System.out.println(ANSI_RED + "No streams were found for: " + channelName + ANSI_RESET);
    }
//    downloadTheFile();
//    pushFileToGithub();
//    createAndMergePR();
    addBackupService();
  }

  public void addBackupService() throws Exception {
    String iptv1 = "mytv.fun", iptv2 = "my34.xyz", iptv32 = "xtream-ui03";
    Date today = new Date();
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    String date = DATE_FORMAT.format(today);
    System.out.println(date);
    System.out.println("https://m.tousecurity.com/free-iptv-links-m3u-playlist-" + date + "/");
    driver.get("https://m.tousecurity.com/free-iptv-links-m3u-playlist-" + date + "/");
    driver.manage().window().maximize();
    Thread.sleep(500);
    if (driver.getPageSource().contains("Autoriser")) {
      driver.findElement(By.xpath("(//p [@class = 'fc-button-label']) [1]")).click();
      Thread.sleep(500);
    }
    if (driver.getPageSource().contains(iptv1)) {
      link = driver.findElement(By.xpath("//p [contains(text(), 'mytv.fun')]")).getText();
      driver.get(link);
    }
    getFileName();
  }

  public void getFileName() throws Exception {
    fileUsername = link.substring(link.indexOf("=") + 1, link.indexOf("&password"));
    dailyIPTVFilename = "tv_channels_" + fileUsername + ".m3u";
    if (link.contains("_plus")) {
      dailyIPTVFilename = dailyIPTVFilename + "_plus";
    }
    System.out.println(dailyIPTVFilename);

    //changeTheFileName
    File IPTVPlaylistFile = new File("E:\\IPTV\\CatIPTVM3u\\src\\M3uDownloads\\" + dailyIPTVFilename);
    File rename = new File("E:\\IPTV\\CatIPTVM3u\\src\\M3uDownloads\\backupTV.m3u");
    boolean flag = IPTVPlaylistFile.renameTo(rename);
    if (flag) {
      System.out.println(ANSI_GREEN + "The file has been renamed to: " + "backupTV.m3u" + ANSI_RESET);
    } else System.out.println(ANSI_RED + "The files has failed to be renamed" + ANSI_RESET);
    File m3uFile = new File(dailyIPTVFilename);
    if (m3uFile.exists()) {
      if (m3uFile.delete()) {
        System.out.println(ANSI_GREEN + "The file has been deleted prior to addition of new file" + ANSI_RESET);
      } else
        throw new Exception("Unable to delete file " + dailyIPTVFilename + ", please check that the file exists prior to deletion");
      System.out.println(ANSI_RED + "M3u file doesn't exist, no need to delete" + ANSI_RESET);
    }
    iptvFlag = false;
  }

  public void openWebPage() throws Exception {
    setChromeOptions();
    driver.get(Utils.BASE_URL);
    driver.manage().window().maximize();
    new IPTVCatPage(driver).getAcceptCookies().click();
    File src = new File("src/channels.xlsx");
    FileInputStream fis = new FileInputStream(src);
    XSSFWorkbook xsf = new XSSFWorkbook(fis);
    XSSFSheet sheet = xsf.getSheet("newList");
    numberOfChannels = sheet.getPhysicalNumberOfRows();
  }

  public void setChromeOptions() {
    System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
    String downloadFilepath = "E:\\IPTV\\CatIPTVM3u\\src\\M3uDownloads";
    HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
    chromePrefs.put("profile.default_content_settings.popups", 0);
    chromePrefs.put("download.default_directory", downloadFilepath);
    ChromeOptions options = new ChromeOptions();
//    options.addArguments("headless");
    options.setExperimentalOption("prefs", chromePrefs);
    DesiredCapabilities cap = DesiredCapabilities.chrome();
    cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    cap.setCapability(ChromeOptions.CAPABILITY, options);
    driver = new ChromeDriver(cap);
  }

  public void excelDataReader() throws Exception {
    //Finds the channel name & location to search
    File src = new File("src/channels.xlsx");
    removeBlankRowsFromExcel();
    FileInputStream fis = new FileInputStream(src);
    XSSFWorkbook xsf = new XSSFWorkbook(fis);
    XSSFSheet sheet = xsf.getSheet("newList");
    channelName = sheet.getRow(channelIterator).getCell(0).getStringCellValue();
    channelLocation = sheet.getRow(channelIterator).getCell(1).getStringCellValue().toLowerCase(Locale.ROOT);
  }

  public void removeBlankRowsFromExcel() throws IOException {
    File src = new File("src/channels.xlsx");
    FileInputStream fis = new FileInputStream(src);
    XSSFWorkbook xsf = new XSSFWorkbook(fis);
    XSSFSheet sheet = xsf.getSheet("newList");

    Boolean isRowEmpty = true;

    for (int i = 0; i < sheet.getLastRowNum(); i++) {
      if (sheet.getRow(i) == null) {
        isRowEmpty = true;
        sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
        i--;
        continue;
      }
      for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
        if (sheet.getRow(i).getCell(j).toString().trim().equals("")) {
          isRowEmpty = true;
        } else {
          isRowEmpty = false;
          break;
        }
      }
      if (isRowEmpty == true) {
        sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
        i--;
      }
    }

  }

  public void addToList() throws Exception {
    //Adds the active links to the downloadable file in IPTVCat
    newIteratorValue = resultsIterator * 2; //45
    if (newIteratorValue >= 20) {
      newIteratorValue = newIteratorValue + 1;
    }
    if (newIteratorValue >= 45) {
      newIteratorValue = newIteratorValue + 2;
    }
    if (newIteratorValue >= 70) {
      newIteratorValue = newIteratorValue + 3;
    }
    if (newIteratorValue >= 95) {
      newIteratorValue = newIteratorValue + 45;
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
      System.out.println("M3u file doesn't exist, no need to delete");
    }
  }

  public void pushFileToGithub() {
    Runtime runtime = Runtime.getRuntime();
    Process p1;
    try {
      {
        if (iptvFlag) {
          p1 = runtime.exec("E:\\IPTV\\CatIPTVM3u\\src\\M3uFilePush.bat");
        } else p1 = runtime.exec("E:\\IPTV\\CatIPTVM3u\\src\\M3uDownloads\\backupTV.m3u");
      }
      InputStream is = p1.getInputStream();
      int i;
      while ((i = is.read()) != -1) {
        System.out.print((char) i);
      }
    } catch (Exception e) {
      System.out.println("Running the batch file has failed");
    }
  }

  public void createAndMergePR() throws InterruptedException, FileNotFoundException {
    //Need amended to remove sleeps - but good for now
    //Encrypt password
    File dataFile = new File("E:\\IPTV\\CatIPTVM3uData\\data.txt");
    Scanner scanner = new Scanner(dataFile);
    var temp1 = scanner.next();
    String githubPassword = temp1.replace("password=", "").trim();

    //Create PR and Merge
    driver.get(Utils.GITHUB_URL);
    driver.manage().window().maximize();
    GithubPage githubPage = new GithubPage(driver);
    Thread.sleep(3000);
    githubPage.getUsername().sendKeys("connord96");
    Thread.sleep(500);
    githubPage.getPassword().sendKeys(githubPassword);
    Thread.sleep(500);
    githubPage.getSubmit().click();
    Thread.sleep(2000);
    driver.navigate().to(Utils.GITHUB_PR_URL);
    Thread.sleep(2000);
    githubPage.getCreatePR().click();
    Thread.sleep(2000);
    githubPage.getCreatePRPage2().click();
    Thread.sleep(3000);
    githubPage.getMergePR().click();
    Thread.sleep(3000);
    githubPage.getMergeComplete().click();
  }
}