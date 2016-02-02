package nl.malmberg.flash_xebium.commands;

public class ClickByIdCommand extends BaseFlashCommand {

    @Override
    protected String getScript(String objectId, String notUsed) {
        return "window.FlashSelenium.clickById('" + objectId + "');";
    }

}
