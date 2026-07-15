package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import org.openqa.selenium.By;
import ru.kduskov.ui.elements.ButtonElement;
import ru.kduskov.ui.elements.InputElement;

public class ChangeNamePage extends BasePage {
    private final InputElement newUsernameInput = new InputElement(Selectors.byAttribute("placeholder", "Enter new name"));

    private final ButtonElement saveChangesButton = new ButtonElement(Selectors.byXpath("//button[contains(text(),'Save Changes')]"));

    @Override
    protected String url() {
        return "/edit-profile";
    }

    @Override
    protected By pageLocator() {
        return Selectors.byText("✏\\uFE0F Edit Profile");
    }

    public ChangeNamePage setNewUsername(String username) {
        newUsernameInput.clearAndSetValue(username);
        return this;
    }

    public ChangeNamePage clickSaveChangesButton() {
        saveChangesButton.click();
        return this;
    }
}
