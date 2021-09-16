package ru.netology.test;

import com.codeborne.selenide.SelenideElement;

import com.google.j2objc.annotations.LoopTranslation;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class CardDeliveryOrderTest {

    String name = DataGenerator.generateName();
    String phone = DataGenerator.generatePhone();
    String city = DataGenerator.generateCity();

    @Test
    void shouldSubmitRequest() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=city] input").setValue(city);
        form.$("[data-test-id=date] input").doubleClick().sendKeys(DataGenerator.generateDate(3));
        form.$("[data-test-id=name] input").setValue(name);
        form.$("[data-test-id=phone] input").setValue(phone);
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        $(".notification_status_ok").shouldBe(visible);
        $("[data-test-id='success-notification'] .notification__content").shouldHave(exactText("Встреча успешно запланирована на " + DataGenerator.generateDate(3)));
        form.$("[data-test-id=date] input").doubleClick().sendKeys(DataGenerator.generateDate(7));
        form.$(".button").click();
        $("[data-test-id=replan-notification]").waitUntil(visible, 15000);
        $(withText("Перепланировать")).click();
        $(".notification_status_ok").shouldBe(visible);
        $(".notification__content").shouldHave(exactText("Встреча успешно запланирована на " + DataGenerator.generateDate(7)));
    }

    @Test
    void shouldEnterAnIncorrectFirstAndLastName() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=city] input").setValue(city);
        form.$("[data-test-id=date] input").doubleClick().sendKeys(DataGenerator.generateDate(3));
        $("[data-test-id=name] input").setValue("Ivanov Ivan");
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(byText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldEnterAnIncorrectCity() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=city] input").setValue("Ухта");
        form.$("[data-test-id=date] input").doubleClick().sendKeys(DataGenerator.generateDate(3));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=city].input_invalid").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldInterAnIncorrectDate() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=city] input").setValue(city);
        form.$("[data-test-id=date] input").doubleClick().sendKeys(DataGenerator.generateDate(-3));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=date]").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldLeaveAllFieldsEmpty() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        $(".button").click();
        $("[data-test-id=city]").shouldHave(exactText("Поле обязательно для заполнения"));
    }
}