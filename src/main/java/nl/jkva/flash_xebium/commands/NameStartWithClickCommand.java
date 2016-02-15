package nl.jkva.flash_xebium.commands;

public class NameStartWithClickCommand extends BaseFlashCommand {
    @Override

    protected String getScript(String objNamePattern, String objIdx) {
        return String.format("return window.FlashSelenium.flashObjNameStartWithClick('%s','%s');", objNamePattern, objIdx);
    }

}
