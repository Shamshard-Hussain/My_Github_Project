package com.restaurant.Restaurant;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriverException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeleniumController {

    @GetMapping("/testLogin")
    public String testLoginPage() {
        String result;
        WebDriver driver = null;

        try {
            // Set the path for ChromeDriver (Optional, if it's not already set in the environment)
            // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

            // Initialize WebDriver (Chrome in this case)
            driver = new ChromeDriver();

            // Open the login page
            driver.get("http://localhost:9090");

            // Maximize the window
            driver.manage().window().maximize();

            // Get the title of the page
            String pageTitle = driver.getTitle();

            // Print the title in green color (in terminal, color codes are not applicable in web response)
            result = "\033[1;32m" + pageTitle + "\033[0m";

            // Return the title in the response (color codes won't work in the response, just text)
            return "Page Title: " + pageTitle;

        } catch (WebDriverException e) {
            result = "Error occurred: " + e.getMessage();
        } finally {
            if (driver != null) {
                // Close the browser window
                driver.quit();
            }
        }

        return result;
    }
}
