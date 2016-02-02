package nl.jkva.flash_xebium.commands;

public class EnterTextCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String objectName, String notUsed) {
        return String.format("window.FlashSelenium.enterText('%s');", objectName);
    }
}
