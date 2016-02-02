package nl.jkva.flash_xebium.commands;

@Deprecated
public class FlashClickCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String notUsed1, String notUsed2) {
        throw new UnsupportedOperationException(String.format(
            "You should not call this operation. Use %s instead ", ClickByNameCommand.class.getSimpleName()));
    }
}
