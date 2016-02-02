package nl.malmberg.flash_xebium.commands;

import com.thoughtworks.selenium.webdriven.SeleneseCommand;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFlashCommand extends SeleneseCommand<String> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseFlashCommand.class);

    @Override
    protected final String handleSeleneseCommand(WebDriver driver, String locator, String value) {
        // Get and run the setup script containing the Selenium Flash test wrapper.
        final String setupScript = getSetupScript();
        final Object setupResult = execute(driver, setupScript);
        LOG.debug("Result of setup script: {}", setupResult);

        // Run the actual script command.
        //TODO: These 2 lines are hard coded for now, for convenience, but have to be removed to make the fixture reusable.
        final String script =
            "window.FlashSelenium.setFlashObjectId('OefenenWebApp');\n" +
            getScript(locator, value);
        final Object scriptResult = execute(driver, script);
        final String result = scriptResult == null ? "" : scriptResult.toString();
        LOG.info(String.format("Result of script: %s", result));
        return result;
    }

    protected abstract String getScript(String locator, String value);

    private static Object execute(WebDriver driver, String script) {
        final JavascriptExecutor executor = (JavascriptExecutor) driver;
        return executor.executeScript(script);
    }

    private static String getSetupScript() {
        return "if (!(window.FlashSelenium)) {\n" +
            "  window.FlashSelenium = (function (document) {\n" +
            "    var self = this;\n" +
            "    self.errors = [];\n" +
            "    self.variables = [];\n" +
            "    self.flashObjectId = null;\n" +
            "    \n" +
            "    self.addError = function (error) {\n" +
            "      errors.push(error);\n" +
            "    };\n" +
            "    \n" +
            "    self.setFlashObjectId = function (id) {\n" +
            "      self.flashObjectId = id;\n" +
            "    };\n" +
            "    \n" +
            "    self.getFlashObjectId = function () {\n" +
            "      return self.flashObjectId;\n" +
            "    };\n" +
            "    \n" +
            "    self.getFlashObject = function () {\n" +
            "      return document.getElementById(self.flashObjectId);\n" +
            "    };\n" +
            "    \n" +
            "    self.findObjectId = function (name) {\n" +
            "      return self.getFlashObject().findID(name);\n" +
            "    };\n" +
            "    \n" +
            "    self.clickById = function (id) {\n" +
            "      return self.getFlashObject().click(id);\n" +
            "    };\n" +
            "    \n" +
            "    self.clickByName = function (name) {\n" +
            "      var id = self.findObjectId(name);\n" +
            "      if (id > 0) {\n" +
            "        return self.clickById(id);\n" +
            "      } else {\n" +
            "        return false;\n" +
            "      }\n" +
            "    };\n" +
            "    \n" +
            "    self.dragAndDrop = function (dragFromName, dropOnName) {\n" +
            "      var dragFromId = self.getFlashObjectId(dragFromName);\n" +
            "      var dropOnId = self.getFlashObjectId(dropOnName);\n" +
            "      if (dragFromId > 0 && dropOnId > 0) {\n" +
            "        self.getFlashObject().dropOn(dragFromId, dropOnId);\n" +
            "        return true;\n" +
            "      } else {\n" +
            "        return false;\n" +
            "      }\n" +
            "    };\n" +
            "    \n" +
            "    self.verifyFlashObjectPresent = function (name) {\n" +
            "      return self.findObjectId(name) > 0;\n" +
            "    };\n" +
            "    \n" +
            "    self.storeFlashObjectPresent = function (name, variable) {\n" +
            "      if (self.verifyObjectPresent(name)) {\n" +
            "        self.variables[name] = true;\n" +
            "      } else {\n" +
            "        self.variables[name] = false;\n" +
            "      }\n" +
            "    };\n" +
            "    \n" +
            "    self.enterText = function (name, value) {\n" +
            "      var id = self.findObjectId(name);\n" +
            "      if (id > 0) {\n" +
            "        self.getFlashObject().enterText(id, value);\n" +
            "        return true;\n" +
            "      } else {\n" +
            "        return false;\n" +
            "      }\n" +
            "    };\n" +
            "    \n" +
            "    self.verifyText = function (name, expectedValue) {\n" +
            "      var id = self.findObjectId(name);\n" +
            "      if (id > 0) {\n" +
            "        return self.getFlashObject().getText(id) === value;\n" +
            "      } else {\n" +
            "        return false;\n" +
            "      }\n" +
            "    };\n" +
            "    \n" +
            "    return {\n" +
            "      errors: self.errors,\n" +
            "      addError: self.addError,\n" +
            "      setTestCaseId: self.setTestCaseId,\n" +
            "      getFlashObjectId: self.getFlashObjectId,\n" +
            "      setFlashObjectId: self.setFlashObjectId,\n" +
            "      findObjectId: self.findObjectId,\n" +
            "      verifyObjectPresent: self.verifyObjectPresent,\n" +
            "      storeObjectPresent: self.storeObjectPresent,\n" +
            "      clickByName: self.clickByName,\n" +
            "      clickById: self.clickById,\n" +
            "      dragAndDrop: self.dragAndDrop,\n" +
            "      enterText: self.enterText,\n" +
            "      verifyText: self.verifyText\n" +
            "    }\n" +
            "  })(document);\n" +
            "}\n";
    }
}
