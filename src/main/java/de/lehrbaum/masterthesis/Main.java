package de.lehrbaum.masterthesis;

import de.lehrbaum.masterthesis.view.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.*;

public class Main extends Application {
	private static final Logger logger = Logger.getLogger("de.lehrbaum.masterthesis");
	public static File loggerFile;

	public static void main(String[] args) {
		initializeLogging();
		//testStuff();
		launch(args);
	}

	private static void initializeLogging() {
		StreamHandler handler = new StreamHandler(System.out, new SimpleFormatter()) {
			@Override
			public synchronized void publish(final LogRecord record) {
				super.publish(record);
				flush();
			}
		};
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		setUpFileLogger();// when running as application set up a file logger.
		Scene scene = new Scene(new MainWindow(), 1100, 750);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Lehrbaum Master Thesis");//localize
		primaryStage.show();
	}

	private static void setUpFileLogger() {
		try {
			File folder = getDirectoryForFiles();

			File logFile = new File(folder, "protokol.log");
			String logfilePath = logFile.getAbsolutePath().replace(File.separatorChar, '/');
			logger.fine(logfilePath);
			Logger mainLogger = Logger.getLogger("de.lehrbaum.masterthesis");
			FileHandler fileHandler = new FileHandler(logfilePath, true);
			fileHandler.setLevel(Level.INFO);
			mainLogger.addHandler(fileHandler);
			loggerFile = logFile;
		} catch(IOException e) {
			logger.log(Level.SEVERE, "Problem setting up logger.", e);
		}
	}

	public static File getDirectoryForFiles() {
		URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			File f = new File(url.toURI()).getParentFile();
			if(f.canRead())
				return f;
		} catch(URISyntaxException e) {
			logger.severe("Problem with the program folder: " + url);
		}
		return new File(System.getProperty("user.dir"));
	}
}
