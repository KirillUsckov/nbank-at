package ru.kduskov.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BankAlerts {
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    SUCCESSFULLY_DEPOSITED_TO_ACCOUNT("✅ Successfully deposited $%s to account %s!"),
    SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT("✅ Successfully transferred $%s to account %s!"),
    FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    DEPOSIT_LESS_OR_EQUAL_TO_5000("❌ Please deposit less or equal to 5000$."),
    NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY("Name must contain two words with letters only"),
    RECIPIENT_NAME_DOES_NOT_MATCH_REGISTERED_NAME("❌ The recipient name does not match the registered name.");
    private String message;
}
