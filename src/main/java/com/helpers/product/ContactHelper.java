package com.helpers.product;

import com.BrowserController;
import com.data.ContactData;
import com.data.ContactDataGenerator;
import com.helpers.WebDriverHelperBase;
import com.utils.ModifiedSortedList;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ContactHelper extends WebDriverHelperBase {

    public static boolean CREATION = true;
    public static boolean MODIFICATION = false;
    private final ModifiedSortedList<ContactData> cachedContacts = new ModifiedSortedList<ContactData>();

    public ContactHelper(BrowserController manager) {
        super(manager);
    }

    public ContactHelper createContact(ContactData contact) {
        openContactPage().fillContactForm(contact, CREATION).submitContactUpdateOrCreation();
        openMainPage();
        manager.getAppModel().addContact(contact);
        rebuildCache();
        return this;
    }

    public ContactHelper deleteContact(int index) {
        openContactDetails(index).submitContactRemoval();
        manager.getAppModel().removeContact(index);
        rebuildCache();
        return this;
    }

    public ContactHelper modifyContact(int index, ContactData contact) {
        openContactDetails(index).fillContactForm(contact, ContactHelper.CREATION).submitContactUpdateOrCreation();
        openMainPage();
        manager.getAppModel().removeContact(index).addContact(contact);
        rebuildCache();
        return this;
    }

    public ModifiedSortedList<ContactData> getUiContacts() {
        if (cachedContacts == null || cachedContacts.isEmpty()) {
            rebuildCache();
        }
        return cachedContacts;
    }

    public void rebuildCache() {
        manager.getNavigationHelper().mainPage();
        //Find all checkboxes
        List<WebElement> checkboxes = driver.findElements(By.name("selected[]"));
        for (WebElement checkbox : checkboxes) {
            String title = checkbox.getAttribute("title");
            // <list> add()
            cachedContacts.add(new ContactData().withFirstName(title.substring("Select (".length(), title.length() - ")".length()).trim()));
        }
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------
     */
    public ContactHelper submitContactRemoval() {
        driver.findElement(By.name("phone2")).sendKeys("");
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        String deleteButton = "//input[@value='Delete']";
        click(By.xpath(deleteButton));
        refreshPage();
        click(By.linkText("home page"));
        cachedContacts.clear();
        return this;
    }

    public ContactHelper submitContactUpdateOrCreation() {
        String customXpath = "//input[@name='submit' or @name='update']";
        driver.findElement(By.xpath(customXpath)).click();
        click(By.linkText("add next"));
        cachedContacts.clear();
        return this;
    }

    public ContactHelper fillContactForm(ContactData contactData, boolean formType) {

        type(By.name("firstname"), contactData.getFirstName());
        type(By.name("lastname"), contactData.getLastName());
        type(By.name("address"), contactData.getAddress1());
        type(By.name("home"), contactData.getHome());
        type(By.name("mobile"), contactData.getMobilephonenumber());
        type(By.name("work"), contactData.getWorkphonenumber());
        type(By.name("email"), contactData.getEmail1());
        type(By.name("email2"), contactData.getEmail2());
        type(By.name("byear"), contactData.getByear());
        type(By.name("address2"), contactData.getSecondaryaddress());
        type(By.name("phone2"), contactData.getSecondaryhomephonenumber());
        if (formType == CREATION) {
            selectFromDropDownList("group 1");
        } else {
            if (driver.findElements(By.name("new_group")).size() != 0) {
                throw new Error("Group selector exists in contact modification form");
            }
        }
        return this;
    }

    public ContactHelper openMainPage() {
        // open main page
        driver.get(manager.baseUrl);
        return this;
    }

    public ContactHelper selectFromDropDownList(String groupName) {
        select(By.name("bday"), "4");
        select(By.name("bmonth"), "April");

        if (driver.findElements(By.name("new_group")).size() != 0) {
            Select sel = new Select(driver.findElement(By.name("new_group")));
            try {
                sel.selectByValue(groupName);
            } catch (Exception e) {
                sel.selectByIndex(1);
            }
        }
        return this;
    }

    public ContactHelper openContactPage() {
        click(By.linkText("add new"));
        //driver.findElement(By.linkText("add new")).click();
        return this;
    }

    public ContactHelper openContactDetails(int index) {
        waitUntilContactListAppear();
        List<WebElement> editLinks = driver.findElements(By.xpath("//a[contains(@href,'edit.php?id')]"));
        editLinks.get(index).click();
        // click(By.xpath("//a[@href='edit.php?id=" + index + "']"));
        return this;

    }

    public void waitUntilPageLoads() {
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    public void waitUntilContactListAppear() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("maintable")));
    }

    private boolean onContactPage() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl.contains("/edit.php") &&
                driver.findElement(By.xpath("//input[@value='Enter' and @type='submit' and @name='submit']")).isDisplayed();
    }

    /* Contact */
    @DataProvider
    public Iterator<Object[]> randomValidContactGenerator() {
        return wrapContactsForDataProvider(ContactDataGenerator.generateRandomContacts(1)).iterator();
    }

    protected List<Object[]> wrapContactsForDataProvider(List<ContactData> listContactData) {
        List<Object[]> list = new ArrayList<Object[]>();
        for (ContactData contact : listContactData) {
            list.add(new Object[]{contact});
        }
        return list;
    }
}
