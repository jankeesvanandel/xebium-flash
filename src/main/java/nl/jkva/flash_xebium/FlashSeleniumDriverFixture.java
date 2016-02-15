package nl.jkva.flash_xebium;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.jkva.flash_xebium.commands.ClickByIdCommand;
import nl.jkva.flash_xebium.commands.ClickByNameCommand;
import nl.jkva.flash_xebium.commands.ClickCommand;
import nl.jkva.flash_xebium.commands.DragAndDropCommand;
import nl.jkva.flash_xebium.commands.EnterTextCommand;
import nl.jkva.flash_xebium.commands.ExecuteCommand;
import nl.jkva.flash_xebium.commands.FlashObjectIdListCommand;
import nl.jkva.flash_xebium.commands.GetFlashObjectIdCommand;
import nl.jkva.flash_xebium.commands.NameStartWithClickCommand;
import nl.jkva.flash_xebium.commands.OutputErrorsCommand;
import nl.jkva.flash_xebium.commands.SetFlashObjectIdCommand;
import nl.jkva.flash_xebium.commands.ShowObjectsCommand;
import nl.jkva.flash_xebium.commands.StoreFlashObjectPresentCommand;
import nl.jkva.flash_xebium.commands.VerifyFlashObjectPresentCommand;
import nl.jkva.flash_xebium.commands.VerifyTextCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.webdriven.SeleneseCommand;
import com.thoughtworks.selenium.webdriven.WebDriverCommandProcessor;
import com.xebia.incubator.xebium.AssertionAndStopTestError;
import com.xebia.incubator.xebium.ExtendedSeleniumCommand;
import com.xebia.incubator.xebium.SeleniumDriverFixture;

public class FlashSeleniumDriverFixture extends SeleniumDriverFixture {

    private static final Logger LOG = LoggerFactory.getLogger(FlashSeleniumDriverFixture.class);

    private static final Map<String, SeleneseCommand<?>> FLASH_COMMANDS = new HashMap<>();

    static {
        FLASH_COMMANDS.put("clickByName", new ClickByNameCommand());
        FLASH_COMMANDS.put("outputErrors", new OutputErrorsCommand());
        FLASH_COMMANDS.put("setFlashObjectId", new SetFlashObjectIdCommand());
        FLASH_COMMANDS.put("getFlashObjectId", new GetFlashObjectIdCommand());
        FLASH_COMMANDS.put("flashClick", new ClickCommand());
        FLASH_COMMANDS.put("flashList", new FlashObjectIdListCommand());
        FLASH_COMMANDS.put("showObjects", new ShowObjectsCommand());
        FLASH_COMMANDS.put("dragAndDrop", new DragAndDropCommand());
        FLASH_COMMANDS.put("verifyFlashObjectPresent", new VerifyFlashObjectPresentCommand());
        FLASH_COMMANDS.put("storeFlashObjectPresent", new StoreFlashObjectPresentCommand());
        FLASH_COMMANDS.put("enterText", new EnterTextCommand());
        FLASH_COMMANDS.put("verifyText", new VerifyTextCommand());
        FLASH_COMMANDS.put("clickById", new ClickByIdCommand());
        FLASH_COMMANDS.put("command", new ExecuteCommand());
        FLASH_COMMANDS.put("flashObjNameStartWithClick", new NameStartWithClickCommand());
    }

    // Initialized together
    private boolean customUserExtensionInitialized = false;
    private Method executeCommand = null;
    private Method checkResult = null;

    public boolean doFlashOn(String command, String target) {
        LOG.info("Performing | " + command + " | " + target + " |");
        lazyInitializeCustomUserExtension();
        return executeDoCommand(makeXebiumBooleanCommand(command), new String[]{target});
    }

    public boolean doFlashOnWith(String command, String target, String value) {
        LOG.info("Performing | " + command + " | " + target + " | " + value);
        lazyInitializeCustomUserExtension();
        return executeDoCommand(makeXebiumBooleanCommand(command), new String[]{target, value});
    }

    public boolean doFlashOnTimesWithDelay(String command, String target, int times, int delay) throws InterruptedException {
        int i = 0;
        while (i < times) {
            i++;
            if (!doFlashOn(command, target)) {
                return false;
            }
            Thread.sleep(delay);
        }
        return true;
    }

    private boolean executeDoCommand(final String methodName, final String[] values) {

        ExtendedSeleniumCommand command = new ExtendedSeleniumCommand(methodName);

        SeleniumCommandResult commandResult = executeAndCheckResult(command, values, 0);

        if (commandResult.failed() && command.isAssertCommand()) {
            throw new AssertionAndStopTestError(commandResult.output);
        }

        if (commandResult.hasException()) {
            throw new AssertionError(commandResult.getException());
        } else {
            return commandResult.result;
        }
    }

    private SeleniumCommandResult executeAndCheckResult(ExtendedSeleniumCommand command, String[] values, long delay) {
        try {
            String output = invokeExecuteCommand(command, values, delay);

            if (command.requiresPolling() || command.isAssertCommand() || command.isVerifyCommand() || command.isWaitForCommand()) {
                String expected = values[values.length - 1];
                boolean result = invokeCheckResult(command, expected, output);
                LOG.info("Command '" + command.getSeleniumCommand() + "' returned '" + output + "' => " + (result ? "ok" : "not ok, expected '" + expected + "'"));

                return new SeleniumCommandResult(result, output, null);
            } else if (command.isBooleanCommand()) {
                String expected = "true";
                boolean result = invokeCheckResult(command, expected, output);
                LOG.info("Command '" + command.getSeleniumCommand() + "' returned '" + output + "' => " + (result ? "ok" : "not ok, expected '" + expected + "'"));

                return new SeleniumCommandResult(result, output, null);
            } else {
                LOG.info("Command '" + command.getSeleniumCommand() + "' returned '" + output + "'");
                return success(output);
            }
        } catch (Exception e) {
            return failure(e);
        }
    }

    private static class SeleniumCommandResult {
        private final boolean result;

        private final String output;

        private final Exception exception;

        private SeleniumCommandResult(boolean result, String output, Exception e) {
            this.result = result;
            this.output = output;
            this.exception = e;
        }

        public boolean failed() {
            return !result;
        }

        public boolean succeeded() {
            return result;
        }

        public boolean hasException() {
            return exception != null;
        }

        public Exception getException() {
            return exception;
        }
    }

    private static SeleniumCommandResult success(String output) {
        return new SeleniumCommandResult(true, output, null);
    }

    private static SeleniumCommandResult failure() {
        return new SeleniumCommandResult(false, null, null);
    }

    private SeleniumCommandResult failure(Exception e) {
        return new SeleniumCommandResult(false, null, e);
    }

    // Put "is" in front of the command to fool Xebium that this is a verify command and it needs to check the result.
    private static String makeXebiumBooleanCommand(final String command) {
        return "is" + Character.toUpperCase(command.charAt(0)) + command.substring(1);
    }

    private void addCustomCommandsToXebium(CommandProcessor commandProcessor) {
        try {
            final Field field = ExtendedSeleniumCommand.class.getDeclaredField("WEB_DRIVER_COMMANDS");
            field.setAccessible(true);
            final Set<String> fieldValue = getFieldValue(field);
            fieldValue.addAll(FLASH_COMMANDS.keySet());

            final WebDriverCommandProcessor webDriverCommandProcessor = (WebDriverCommandProcessor) commandProcessor;
            for (Map.Entry<String, SeleneseCommand<?>> flashCommand : FLASH_COMMANDS.entrySet()) {
                webDriverCommandProcessor.addMethod(makeXebiumBooleanCommand(flashCommand.getKey()), flashCommand.getValue());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> getFieldValue(Field field) throws IllegalAccessException {
        return (Set<String>) field.get(null);
    }

    private String invokeExecuteCommand(Object... args) {
        try {
            return (String) executeCommand.invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeCheckResult(Object... args) {
        try {
            return (boolean) checkResult.invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Method makeMethodAccessible(String name, Class<?>... parameterTypes) {
        try {
            final Method method = SeleniumDriverFixture.class.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private CommandProcessor lazyInitializeCustomUserExtension() {
        final CommandProcessor commandProcessor = super.getCommandProcessor();
        if (!customUserExtensionInitialized) {
            addCustomCommandsToXebium(commandProcessor);
            executeCommand = makeMethodAccessible("executeCommand", ExtendedSeleniumCommand.class, String[].class, Long.TYPE);
            checkResult = makeMethodAccessible("checkResult", ExtendedSeleniumCommand.class, String.class, String.class);
            customUserExtensionInitialized = true;
        }
        return commandProcessor;
    }

}
