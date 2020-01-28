package consultant;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class consultantTest {
    static WebDriver driver;

    @Test
    void mainTest() throws InterruptedException {


//      Указываем, где лежит драйвер для Chrome.
        System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");

//      Обьявляем инстанс драйвера
        driver = new ChromeDriver();

//      Передаем в инстанс ссылку на тестовую версию Консультанта
        driver.get("http://base.consultant.ru/cons");

//      1. Выполнить поиск документов по фразе «нк ч2».

//      В поле поиска вводим текст "нк ч2"
        driver.findElement(By.id("dictFilter"))
                .sendKeys("нк ч2");

/*      Посылаем с клавиатуры ENTER - вдруг изменится название кнопки поиска, хотя могли бы использовать
        driver.findElement(By.xpath("//*[contains(text(),'Найти')]")).click(); */
        driver.findElement(By.id("dictFilter")).sendKeys(Keys.RETURN);

//      Ждем пока появятся результаты поиска
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("row")));

//      Начинаем замер времени загрузки
        long startOfLoading = System.currentTimeMillis();

//      2. Открыть первый документ из списка результатов поиска.

//      Кликаем на первую ссылку
        driver.findElements(By.className("row")).get(0).click();

/*      3. Проверить, что время открытия документа из списка, включая загрузку текста, занимает не более
        10 секунд. */

//      Переключаем драйвер на открывшееся окно
        String parentWindow = driver.getWindowHandle();

        Set<String> handles =  driver.getWindowHandles();
              for(String windowHandle  : handles)
        {
            if(!windowHandle.equals(parentWindow))
            {
                driver.switchTo().window(windowHandle);
            }
        }

//      Дожидаемся текста
        WebDriverWait wait2 = new WebDriverWait(driver, 11);
            wait2.until(
            ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//*[@id=\"mainContent\"]/div[4]/iframe")));

//      Фиксируем окончаение загрузки документа
        long finishOfLoading = System.currentTimeMillis();

//      Считаем время загрузки документа
        long totalTime = finishOfLoading - startOfLoading;

//      Фиксируем значение, за которое нельзя выходить
        long planedTime = 10000;

//      Проверяем , что не вышли за границу времени
        assertTrue(totalTime < planedTime );

/*      4. Проверить, что открылся правильный документ по наличию текста «налоговый кодекс» и текста
«часть вторая» (без учета регистра букв):
        a. в заголовке в тексте открытого документа;
        b. в названии вкладки браузера.*/

        Thread.sleep(10000);

//      Получаем текст заголовка и приводим к нижнему регистру.
        String documentTitle = driver.findElement(By.cssSelector("#p4"))
                .getText().toLowerCase();

//      Проверяем, что там текст "налоговый кодекс".
        assertTrue(documentTitle.contains("налоговый кодекс"));

//      Получаем текст заголовка и приводим к нижнему регистру.
        String documentTitle2 = driver.findElement(By.cssSelector("#p6"))
                .getText().toLowerCase();

//      Проверяем, что там текст "часть вторая".
        assertTrue(documentTitle2.contains("часть вторая"));

//      Получаем текст вкладки.
        String textOfTab = driver.getTitle().toLowerCase();

//      Проверяем, что там нужный текст.
            assertTrue(textOfTab.contains("налоговый кодекс"));
            assertTrue(textOfTab.contains("часть вторая"));

//      5.Проверить, что в панели «Поиск в тексте» находится поисковая фраза «нк ч2».

//      Выходим из iframe.
        driver.switchTo().defaultContent();

//      получаем значение атрибута поля поиска
        String searchText = driver.findElement(By.id("dictFilter"))
                .getAttribute("value");

//      проверяем на содержание текста "нк ч2"
        assertTrue(searchText.contains("нк ч2"));

//      6.Открыть оглавление, выполнить поиск по оглавлению «статья 163», перейти по оглавлению на эту
//      статью в тексте документа.

//      Нажимаем на кнопку "Оглавление"
        driver.findElement(By.className("contents"))
                .click();

//      Дожидаемся пока документ загрузится
        WebDriverWait wait3 = new WebDriverWait(driver, 10);
        wait3.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page2\"]/div[3]")));

//      Вводим текст "статья 163"
        driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[4]/div[3]/div[1]/div/div/div/table/tbody/tr/td[1]/form/div/input"))
                .sendKeys("статья 163");

//      Посылаем клавишу "Enter"
        driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[4]/div[3]/div[1]/div/div/div/table/tbody/tr/td[1]/form/div/input"))
                .sendKeys(Keys.RETURN);

//      Дожидаемся пока документ загрузится
        WebDriverWait wait4 = new WebDriverWait(driver, 10);
        wait4.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div[4]/div[4]/div[26]/div/div[5]")));

//      Нажимаем на статью
        driver.findElement(By.xpath("/html/body/div[2]/div[3]/div[4]/div[4]/div[26]/div/div[5]"))
                .click();

//      7.Выделить в тексте документа весь текст статьи 163, нажать кнопку «Печать» (с иконкой в виде
//      принтера) на верхней панели.

//      Переключаемся на iframe
        WebDriverWait wait5 = new WebDriverWait(driver, 10);
        wait5.until(
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//*[@id=\"mainContent\"]/div[4]/iframe")));

//      Дожидаемся пока нужный div станет доступен

        WebDriverWait wait6 = new WebDriverWait(driver, 10);
        wait6.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#p1568")));

//      Отмечаем "начало" текста
        WebElement draggable = driver.findElement(By.xpath("/html/body/div[3]/div[3]/div[7]/div[152]"));

//      Отмечаем "конец" текста
        WebElement target = driver.findElement(By.xpath("/html/body/div[3]/div[3]/div[7]/div[154]"));

//      Выполняем "выделение" текста
        new Actions(driver).dragAndDrop(draggable, target).perform();

//      Выходим из iframe, чтобы добраться до кнопки "печать".
        driver.switchTo().defaultContent();

//      Нажимаем на кнопку.
        driver.findElement(By.className("print")).click();
    }

    @AfterAll
//  После всех тестов закрываем драйвер.
    static void afterAll() {
        driver.quit();
    }
}