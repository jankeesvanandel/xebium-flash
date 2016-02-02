package nl.malmberg.flash_xebium.commands;

public class VerifyTextCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String objectName, String expectedValue) {
        return String.format("window.FlashSelenium.verifyText('%s', '%s');", objectName, expectedValue);
    }
}
