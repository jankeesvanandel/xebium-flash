package nl.jkva.flash_xebium.commands;

public class DragAndDropCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String dragFromName, String dropOnName) {
        return String.format("window.FlashSelenium.dragAndDrop('%s', '%s');", dragFromName, dropOnName);
    }
}
