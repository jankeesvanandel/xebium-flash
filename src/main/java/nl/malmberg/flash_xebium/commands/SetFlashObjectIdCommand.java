package nl.malmberg.flash_xebium.commands;

public class SetFlashObjectIdCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String flashObjectId, String notUsed) {
        return "window.FlashSelenium.setFlashObjectId('" + flashObjectId + "');";
    }
}
