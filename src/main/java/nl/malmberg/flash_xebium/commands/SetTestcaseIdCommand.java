package nl.malmberg.flash_xebium.commands;

public class SetTestcaseIdCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String testCaseId, String notUsed) {
        return "window.FlashSelenium.setTestCaseId('" + testCaseId + "');";
    }
}
