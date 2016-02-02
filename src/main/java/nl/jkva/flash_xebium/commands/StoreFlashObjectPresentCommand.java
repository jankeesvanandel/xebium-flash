package nl.jkva.flash_xebium.commands;

public class StoreFlashObjectPresentCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String objectName, String variableName) {
        return String.format("window.FlashSelenium.storeFlashObjectPresent('%s', '%s');", objectName, variableName);
    }
}
