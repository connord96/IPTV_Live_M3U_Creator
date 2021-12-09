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
//
//  public WebElement getSubmit() {
//    return driver.findElement(By.xpath("//input [@name='commit']"));
//  }
//
//  public WebElement getSubmit() {
//    return driver.findElement(By.xpath("//input [@name='commit']"));
//  }
//
//  public WebElement getSubmit() {
//    return driver.findElement(By.xpath("//input [@name='commit']"));
//  }
//
//  public WebElement getSubmit() {
//    return driver.findElement(By.xpath("//input [@name='commit']"));
//  }
//
//  public WebElement getSubmit() {
//    return driver.findElement(By.xpath("//input [@name='commit']"));
//  }
//
//  public WebElement getSubmit() {
//    return driver.findElement(By.xpath("//input [@name='commit']"));
//  }




}
