from selenium import webdriver
from selenium.common.exceptions import WebDriverException, NoSuchElementException
from selenium.webdriver.common.by import By
import time

def perform_registration(driver, first_name, last_name, email, phone, password, confirm_password):
    try:
        # Locate and fill the input fields
        driver.find_element(By.NAME, "firstName").clear()
        driver.find_element(By.NAME, "firstName").send_keys(first_name)
        
        driver.find_element(By.NAME, "lastName").clear()
        driver.find_element(By.NAME, "lastName").send_keys(last_name)
        
        driver.find_element(By.NAME, "email").clear()
        driver.find_element(By.NAME, "email").send_keys(email)
        
        driver.find_element(By.NAME, "phone").clear()
        driver.find_element(By.NAME, "phone").send_keys(phone)
        
        driver.find_element(By.NAME, "password").clear()
        driver.find_element(By.NAME, "password").send_keys(password)
        
        driver.find_element(By.NAME, "confirmPassword").clear()
        driver.find_element(By.NAME, "confirmPassword").send_keys(confirm_password)

        # Submit the form
        driver.find_element(By.XPATH, "//button[@type='submit']").click()
        
    except NoSuchElementException as e:
        print("Element not found during registration:", e)

def test_registration(driver, first_name, last_name, email, phone, password, confirm_password, description):
    try:
        perform_registration(driver, first_name, last_name, email, phone, password, confirm_password)
        
        # Wait for the response
        time.sleep(4)
        
        # Check for error messages
        try:
            error_message = driver.find_element(By.CSS_SELECTOR, "div[style='color: red;']").text
            print(f"\x1b[6;30;41m{description} - Error: {error_message}\x1b[0m")
        except NoSuchElementException:
            print(f"\x1b[6;30;41m{description} - Error message not found\x1b[0m")
        
    except WebDriverException as e:
        print("WebDriverException:", e)

def main():
    try:
        # Initialize Chrome WebDriver
        driver = webdriver.Chrome()

        # Open the registration page
        driver.get("http://localhost:9090/Register")  
        driver.maximize_window()
        
        # Test invalid first name
        test_registration(driver, "", "ValidLastName", "valid@example.com", "1234567890", "ValidPass1$", "ValidPass1$", "Invalid first name")
        
        # Test invalid last name
        test_registration(driver, "ValidFirstName", "", "valid@example.com", "1234567890", "ValidPass1$", "ValidPass1$", "Invalid last name")
        
        # Test invalid email
        test_registration(driver, "ValidFirstName", "ValidLastName", "invalidemail", "1234567890", "ValidPass1$", "ValidPass1$", "Invalid email")
        
        # Test invalid phone number
        test_registration(driver, "ValidFirstName", "ValidLastName", "valid@example.com", "12345", "ValidPass1$", "ValidPass1$", "Invalid phone number")
        
        # Test invalid password
        test_registration(driver, "ValidFirstName", "ValidLastName", "valid@example.com", "1234567890", "short", "short", "Invalid password")
        

        # Wait for 3 seconds after the last test
        time.sleep(3)

    finally:
        # Close the browser
        driver.quit()

if __name__ == "__main__":
    main()
