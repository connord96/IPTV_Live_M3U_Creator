import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class IPTVCatPage extends PageObject {

  public IPTVCatPage(WebDriver driver) {
    super(driver);
  }

  public WebElement getSearchInput() {
    return driver.findElement(By.xpath("//input [@id='get_stream']"));
  }

  public WebElement getSubmitSearchButton() {
    return driver.findElement(By.xpath("//span [@class='input-group-addon do_search']"));
  }
}
