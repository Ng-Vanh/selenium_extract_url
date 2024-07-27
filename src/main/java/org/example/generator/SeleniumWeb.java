package org.example.generator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumWeb {
    static final String CHROME_DRIVER = "C:\\Windows\\chromedriver.exe";
    static final String WEB_DRIVER = "webdriver.chrome.driver";
    private String url;
    private WebDriver driver;
    private void Init(){
        driver.get(url);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public SeleniumWeb(String url) {
        this.url = url;
        System.setProperty(WEB_DRIVER, CHROME_DRIVER);
        this.driver = new ChromeDriver();
        Init();
    }
    public void execute(String action, String selectType, String selectValue, String inputValue) {
        try {
            // Thực thi lệnh Selenium
            switch (action) {
                case "click":
                    // Ví dụ click vào phần tử với id "button1"
                    switch (selectType) {
                        case "byID":
                            driver.findElement(By.id(selectValue)).click();
                            break;
                        case "byCssSelector":
                            driver.findElement(By.cssSelector(selectValue)).click();
                            break;
                        default:
                            break;
                    }
                    break;
                case "input":
                    switch (selectType) {
                        case "byID":
                            driver.findElement(By.id(selectValue)).sendKeys(inputValue);
                            break;
                        case "byCssSelector":
                            driver.findElement(By.cssSelector(selectValue)).sendKeys(inputValue);
                            break;
                        case "name":
                            driver.findElement(By.name(selectValue)).sendKeys(inputValue);
                        default:
                            break;
                    }
                    break;
                default:
                    System.out.println("Command not recognized.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void end() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }
}
