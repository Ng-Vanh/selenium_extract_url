package org.example.extract;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XPathExtractor {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Windows\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.w3schools.com/w3css/tryw3css_templates_band.htm");
        System.out.println(driver.getTitle());
        try {

            List<WebElement> allElements = driver.findElements(By.xpath("//*"));


            for (WebElement element : allElements) {
                Map<String,String> att = getElementAttributes(driver,element);
                String xpath = getElementXPath(driver, element);
                String id = element.getAttribute("id");
                System.out.println(id);
            }
        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
    private static String getElementXPath(WebDriver driver, WebElement element) {
        String js = "function getElementXPath(element) {" +
                "var paths = [];" +
                "for (; element && element.nodeType == 1; element = element.parentNode) {" +
                "var index = 0;" +
                "for (var sibling = element.previousSibling; sibling; sibling = sibling.previousSibling) {" +
                "if (sibling.nodeType == Node.DOCUMENT_TYPE_NODE) {" +
                "continue;" +
                "}" +
                "if (sibling.nodeName == element.nodeName) {" +
                "++index;" +
                "}" +
                "}" +
                "var tagName = element.nodeName.toLowerCase();" +
                "var pathIndex = (index ? \"[\" + (index + 1) + \"]\" : \"\");" +
                "paths.splice(0, 0, tagName + pathIndex);" +
                "}" +
                "return paths.length ? \"/\" + paths.join(\"/\") : null;" +
                "}" +
                "return getElementXPath(arguments[0]).toLowerCase();";
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(js, element);
    }
    private static Map<String, String> getElementAttributes(WebDriver driver, WebElement element) {
        Map<String, String> attributes = new HashMap<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Map<String, Object> attributeMap = (Map<String, Object>) js.executeScript(
                "var items = {}; " +
                        "for (index = 0; index < arguments[0].attributes.length; ++index) " +
                        "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; " +
                        "return items;", element);

        for (Map.Entry<String, Object> entry : attributeMap.entrySet()) {
            attributes.put(entry.getKey(), entry.getValue().toString());
        }

        return attributes;
    }
}
