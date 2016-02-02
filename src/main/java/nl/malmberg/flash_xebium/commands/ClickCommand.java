package nl.malmberg.flash_xebium.commands;

@Deprecated
public class ClickCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String notUsed1, String notUsed2) {
        throw new UnsupportedOperationException("Use ClickByName instead");
    }
}
