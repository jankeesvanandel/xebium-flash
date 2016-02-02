package nl.jkva.flash_xebium.commands;

@Deprecated
public class FlashObjectIdListCommand extends BaseFlashCommand {
    @Override
    protected String getScript(String notUsed1, String notUsed2) {
        throw new UnsupportedOperationException("Calling this operation is useless, as there is always one (or zero) flash objects");
    }
}
