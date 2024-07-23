package org.example.XPathCollector;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XPathCollector {
    static final String CHROME_DRIVER = "C:\\Windows\\chromedriver.exe";
    static final String WEB_DRIVER = "webdriver.chrome.driver";
    static final String URL = "https://baomoi.com/";
    public static final WebDriver driver = new ChromeDriver();

    public static String getElementXPath(WebDriver driver, WebElement element) {
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

    public static Map<String, String> getElementAttributes(WebDriver driver, WebElement element) {
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

    public static boolean isInteractive(WebElement element) {
        String tagName = element.getTagName();
        String type = element.getAttribute("type");

        return tagName.equals("a") || tagName.equals("button") ||
                (tagName.equals("input") && (type.equals("button") || type.equals("submit") || type.equals("reset") || type.equals("text") || type.equals("password") || type.equals("email"))) ||
                tagName.equals("select") || tagName.equals("textarea") ||
                element.getAttribute("onclick") != null || element.getAttribute("onmouseover") != null;
    }

    public static List<String> getAllXPaths(){
        System.setProperty(WEB_DRIVER, CHROME_DRIVER);
        driver.get(URL);
        List<String> res = new ArrayList<>();
        try {
            List<WebElement> allElements = driver.findElements(By.xpath("//*"));
            for (WebElement element : allElements) {
//                String xpath = getElementXPath(driver, element);
//                res.add(xpath);
                if (isInteractive(element)) {
                    String xpath = getElementXPath(driver, element);
                    res.add(xpath);
                }
            }
            return res;
        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        } finally {
            // driver.quit();
        }
        return res;
    }

    public static void main(String[] args) {
        List<String> xpaths = getAllXPaths();
        for (String xpath : xpaths) {
            System.out.println(xpath);
        }
        driver.quit();
    }
}
