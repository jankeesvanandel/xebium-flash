package nl.jkva.flash_xebium.commands;

public class OutputErrorsCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String notUsed1, String notUsed2) {
        return "window.FlashSelenium.getErrors()";
    }
}
