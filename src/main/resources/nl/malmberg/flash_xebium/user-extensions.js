/*
 (C) Copyright Malmberg BAPS 3.0
 Creator: Thé Nguyen
 Created date:  22-12-2014
 Modified date: 01-12-2015
 Version: 0.6

 */

/**
 * The ID of the Flash Object we are targeting.
 * Defaults to 'OefenenWebApp'.
 * To target another environment add the following to your Selenium test-case:
 * <p>
 * &lt;tr&gt;
 * &lt;td&gt;setEnvironment&lt;/td&gt;
 * &lt;td&gt;Develop_exercises&lt;/td&gt;
 * &lt;td&gt;&lt;/td&gt;
 * &lt;/tr&gt;</p>
 */
Selenium.prototype.environment = "OefenenWebApp";

Selenium.prototype.testcaseID = "baps-client";

/**
 * Stores the current scope (selenium)
 */
var scope;

/**
 * Stores the logged errors.
 * @type {Array}
 */
Selenium.prototype.errors = [];

/**
 * Logs the errors.
 * @param message   The error message.
 */
Selenium.prototype.logError = function(message) {
  this.errors.push({testcaseID:this.testcaseID.toString(), message:message});

  this.doEcho("logError: logged: '" + this.errors[this.errors.length -1].testcaseID + "', '" + this.errors[this.errors.length-1].message + "'.");
}

/**
 * Outputs the errors by throwing an error in the Selenium-IDE.
 */
Selenium.prototype.doBapsOutputErrors = function() {
  var output = "WARNING - the following errors were found:";
  if(scope == undefined) scope = this;

  var testcaseID;

  for(var i = 0; i<this.errors.length; i++) {
    if(this.errors[i].testcaseID != testcaseID) {
      testcaseID = this.errors[i].testcaseID.toString();
      output += "\n" + testcaseID;
    }

    output += "\n" + i + ": " + this.errors[i].testcaseID + " :: " + this.errors[i].message;
  }
  if(this.errors.length > 0) {
    this.doEcho(output);
    throw new Error(output);
  } else {
    this.doEcho("No errors were found.");
  }
}

/**
 * Sets a unique ID to be added to every error logged.
 * This way we can point in which test-case the error was logged.
 *
 * @param value
 */
Selenium.prototype.doBapsSetTestcaseID = function(value) {
  this.testcaseID = value;
  this.doEcho("doSetTestcaseID: " + this.testcaseID);
}

/**
 * Sets the environment (ID of the Flash-object in the HTML page)
 * @param value
 */
Selenium.prototype.doBapsSetEnvironment = function (value) {
  this.environment = value;
  result = true;
}

/**
 * Outputs the environment (ID of the Flash-object in the HTML page) to the
 * Selenium-IDE
 */
Selenium.prototype.doBapsGetEnvironment = function () {
  this.doEcho("Current environment: " + this.environment);
  result = true;
}

/**
 * Requests the object ID, for a named object, from the Flash application.
 * @param objName   The name of the Flash object we want to know the ID of.
 */
Selenium.prototype.findID = function(objName) {
  scope.doEcho("findID: name = " + objName + ", id = " + scope.getFlashObj().findID(objName));
  return scope.getFlashObj().findID(objName);
}

/**
 * Finds the current Flash application (SWF-object) in the HTML page.
 * @returns {*}
 */
Selenium.prototype.getFlashObj = function () {
  if(scope == undefined) scope = this;

  var obj = scope.browserbot.getCurrentWindow().document[scope.environment];
  if (obj == undefined) {
    scope.doEcho("WARNING: could not find Flash object with id '" + this.environment + "'.");
  }

  var flashObj = scope.browserbot.getCurrentWindow().document[this.environment];
  if(flashObj == undefined) {
    this.logError("Flash object with id '" + this.environment + "' could not be found.");
  }
  return flashObj;
}

/**
 * Click on the Flash object whose ID has been determined.
 * <p>Typically this method should not called directly from the IDE
 * test-cases. Use <b>doBapsFlashObjNameClick</p> instead.
 * @param objId
 */
Selenium.prototype.doBapsFlashClick = function (objId) {
  if(scope == undefined) scope = this;

  var flashObj = scope.getFlashObj();
  if(flashObj) flashObj.click(objId);
}

Selenium.prototype.doBapsFlashList = function () {
  this.getFlashObj().list();
}

// Generic functions
Selenium.prototype.doBapsShowObjects = function () {
  var result;

  var doc = this.browserbot.getCurrentWindow().document;
  var ObjList = this.doFlashList();
  window.confirm(JSON.stringify(ObjList));

}
Selenium.prototype.doBapsDragAndDropFlashObj = function (DragObjName, DropObjName) {
  var result;

  var dragObjId = 0;
  var dropObjId = 0;

  this.flashFindID(function(objID) {
    dragObjId = objID;
  }, DragObjName);

  this.flashFindID(function(objID) {
    dropObjId = objID;
  }, DropObjName);


  if (dragObjId > 0 && dropObjId > 0) {
    this.getFlashObj().dropOn(dragObjId, dropObjId);
    result = true;
  }
  else {
    result = false;
  }
}

/**
 * Checks if the named object can be found in the Flash application.
 * @param objName
 */
Selenium.prototype.doBapsVerifyFlashObjPresent = function (objName) {
  var objId;
  this.flashFindID(function(value) {
    objId = value;
  }, objName);
}

Selenium.prototype.doBapsStoreFlashObjPresent = function (objName, varName) {
  var objId;
  this.flashFindID(function(value) {
    objId = value;
    if(objId > 0) {
      storedVars[varName] = true;
    } else {
      storedVars[varName] = false;
    }
  }, objName);
}

Selenium.prototype.doBapsEnterTextFlashObj = function (ObjName, TextValue) {
  var flashObj = this.getFlashObj();

  var objId;
  this.flashFindID(function(value) {
    objId = value;
    if(objId > 0) flashObj.enterText(objId, TextValue)
  }, objName);
}

Selenium.prototype.doBapsVerifyTextFlashObj = function (TextObjName, ExpectedValue) {

  var flashObj = this.getFlashObj();

  var objId;
  this.flashFindID(function(value) {
    objId = value;
    if(objId > 0 && (flashObj.getText(objId) != ExpectedValue) ) {
      scope.logError("The text for field '" + TextObjName + "' does not match the expected value '" + ExpectedValue + "'.");
    }
  }, TextObjName);
}

Selenium.prototype.doBapsFlashObjIdClick = function (objId) {
  this.doBapsFlashClick(objId)
}

/**
 *
 * @param objName   The name of the Flash object we want to click
 */
Selenium.prototype.doBapsFlashObjNameClick = function (objName) {

  scope = this;

  // Get the ID of the named object before trying to click it.

  this.flashFindID(this.doBapsFlashClick, objName);

}

/**
 * Retrieves the ID of a named object in Flash.
 *
 * @param callback  Method that will be called once we have found the ID or have reached our timeout
 * @param objName   The name of the Flash object whose ID we want to retrieve
 */
Selenium.prototype.flashFindID = function(callback, objName) {

  var flashObj = this.getFlashObj();
  var objId;

  if(flashObj) objId = flashObj.findID(objName);

  // Run the command once
  // As we cannot stop the test-case we cannot check the return value repeatedly (see below)
  if(objId > 0) {
    callback(objId);
  } else {
    this.logError("flashFindID: could not find objId for '" + objName + "'.");
  }

}

// TODO: add sleep to doCommand method: if not: Selenium will move on to next action in the IDE!!!
Selenium.prototype.doBapsCommand = function(command, callback, value, repeatCount) {
  if(repeatCount == undefined) repeatCount = 0;
  var objId = command(value);

  scope.doEcho("doCommand: " + objId);

  // Run the command repeatedly
  if(objId > 0) {
    callback(objId);
  } else {
    repeatCount++;
    if(repeatCount > 4) {
      callback(null);
    } else {
      scope.doEcho("Run command again after n seconds...");
      setTimeout(scope.doCommand, 2000, command, callback, value, repeatCount);
    }
  }
}

Selenium.prototype.doBapsFlashObjNameStartWithClick = function (ObjNamePattern, objIdx) {
  var doc = this.browserbot.getCurrentWindow().document;
  var ObjName = "";
  var ObjList = this.getFlashObj().list();
  var ObjNameList = JSON.parse(JSON.stringify(ObjList));

  var ObjNameArr = [];
  for (var prop in ObjNameList) {
    ObjNameArr.push(ObjNameList[prop]);
  }

  var nrObjs = ObjNameArr.length;
  var arrIdx = 0;
  var matchCounter = 0;
  var notFound = true;
  while (arrIdx < nrObjs && notFound) {
    ObjName = ObjNameArr[arrIdx];

    if (ObjName.startsWith (ObjNamePattern)) {
      matchCounter = matchCounter + 1;
      if (matchCounter == objIdx) {
        // Return this objectname.
        notFound = false;

        this.doBapsFlashObjNameClick(ObjName);
      }
    }
    // Try the next objectname
    arrIdx = arrIdx + 1;
  } // endloop
}
