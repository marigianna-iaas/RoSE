package de.unistuttgart.iaas.bpmn.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggerConfig {

	public static final void init() throws Throwable{
		LogManager logManager = LogManager.getLogManager();
		Logger LOGGER = Logger.getLogger("LoggerConfig");
		
		try {
			logManager.readConfiguration(new FileInputStream("./logconfig.properties"));
		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error in loading configuration",exception);
			throw new Throwable("Error in loading configuration: This aplication needs to be terminated");
		}

	}
	
	public static final void initCycleLogger() throws Throwable{
		LogManager logManager = LogManager.getLogManager();
		Logger LOGGER = Logger.getLogger("LoggerConfig");
		
		try {
			logManager.readConfiguration(new FileInputStream("./cyclelogconfig.properties"));
		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error in loading configuration",exception);
			throw new Throwable("Error in loading configuration: This aplication needs to be terminated");
		}

	}
}
