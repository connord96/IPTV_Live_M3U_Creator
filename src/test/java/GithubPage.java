import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GithubPage extends PageObject {

  public GithubPage(WebDriver driver) {
    super(driver);
  }

  public WebElement getSignIn() {
    return driver.findElement(By.xpath("[href = '/login']"));
  }

  public WebElement getUsername() {
    return driver.findElement(By.xpath("//input [@name='login']"));
  }

  public WebElement getPassword() {
    return driver.findElement(By.xpath("//input [@name='password']"));
  }

  public WebElement getSubmit() {
    return driver.findElement(By.xpath("//input [@name='commit']"));
  }

  public WebElement getCreatePR() {
    return driver.findElement(By.xpath("//button [contains(text(), 'Create pull request')]"));
  }

  public WebElement getCreatePRPage2() {
    return driver.findElement(By.xpath("//form //button [contains(text(), 'Create pull request')] [1]"));
  }

  public WebElement getMergePR() {
    return driver.findElement(By.xpath("//button [contains(text(), 'Merge pull request')]"));
  }

  public WebElement getMergeMessage() {
    return driver.findElement(By.xpath("//textarea [@id = 'merge_message_field']"));
  }

  public WebElement getMergeComplete() {
    return driver.findElement(By.xpath("//form //button [@value = 'merge']"));
  }
}
