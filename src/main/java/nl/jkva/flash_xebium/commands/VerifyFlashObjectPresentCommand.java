package nl.jkva.flash_xebium.commands;

public class VerifyFlashObjectPresentCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String objectName, String notUsed) {
        return String.format("window.FlashSelenium.verifyFlashObjectPresent('%s');", objectName);
    }
}
