package nl.malmberg.flash_xebium.commands;

public class ClickByNameCommand extends BaseFlashCommand {

    @Override
    protected String getScript(String objectName, String notUsed) {
        return String.format("return window.FlashSelenium.clickByName('%s');", objectName);
    }

}
