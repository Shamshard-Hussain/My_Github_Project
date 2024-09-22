from selenium import webdriver
from selenium.common.exceptions import WebDriverException, NoSuchElementException
from selenium.webdriver.common.by import By
import time

def perform_login(driver, email, password):
    try:
        # Locate the email input field and enter an email
        email_input = driver.find_element(By.ID, "email")
        email_input.send_keys(email)

        # Locate the password input field and enter a password
        password_input = driver.find_element(By.ID, "password")
        password_input.send_keys(password)

        # Submit the form by clicking the Log In button
        login_button = driver.find_element(By.XPATH, "//button[@type='submit']")
        login_button.click()
        
    except NoSuchElementException as e:
        print("Element not found during login:", e)

def test_login(driver, email, password, expected_success=True):
    try:
        perform_login(driver, email, password)
        
        # Wait to allow the login to process
        time.sleep(5)
        
        if expected_success:
            # Check for successful login 
            if driver.current_url == "http://localhost:9090/user/userHome": 
                print("\x1b[6;30;42mLogin successful\x1b[0m")
            else:
                print("\x1b[6;30;41mLogin failed\x1b[0m")
        else:
            # Check for login failure 
            try:
                error_message = driver.find_element(By.CSS_SELECTOR, "p[style='color: red;']").text
                print("\x1b[6;30;41mError:", error_message, "\x1b[0m")
            except NoSuchElementException:
                print("\x1b[6;30;41mError message not found\x1b[0m")
        
    except WebDriverException as e:
        print("WebDriverException:", e)

def main():
    try:
        # Initialize Chrome WebDriver
        driver = webdriver.Chrome()

        # Open the login page
        driver.get("http://localhost:9090/login")  
        driver.maximize_window()
        time.sleep(2)

        # Test login with invalid credentials first
        test_login(driver, "invaliduser@example.com", "wrongpassword", expected_success=False)

        # Navigate back to login page if needed
        driver.get("http://localhost:9090/login") 
        time.sleep(2)

        # Test successful login
        test_login(driver, "youremail@gmail.com", "password")

        # Wait before closing the browser
        time.sleep(3)

    finally:
        # Close the browser
        driver.quit()

if __name__ == "__main__":
    main()
