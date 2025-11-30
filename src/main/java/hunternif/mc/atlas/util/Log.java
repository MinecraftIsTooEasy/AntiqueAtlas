package hunternif.mc.atlas.util;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

import java.util.logging.Level;

/**
 * A simple logging helper that is not directly dependent on any mod classes.
 * Make sure to call {@link #setModID(String modID)} during the mod's init.
 * @author Hunternif
 */
public class Log {
	private static String modID;
	
	public static void setModID(String modID) {
		Log.modID = modID;
	}
	
	public static void log(Level level, Throwable ex, String msg, Object ... data) {
		if (modID != null) {
			FMLLog.log(modID, level, ex, msg, data);
		} else if (FMLRelaunchLog.log.getLogger() != null){
			FMLLog.log(level, ex, msg, data);
		} else {
			System.out.println("Logger Not Initialized. Falling back to STDOUT:\n\t"+msg);
		}
	}
	
	public static void debug(String msg, Object ... data) {
		log(Level.OFF, null, msg, data);
	}
	
	public static void info(String msg, Object ... data) {
		log(Level.INFO, null, msg, data);
	}
	
	public static void warn(String msg, Object ... data) {
		log(Level.WARNING, null, msg, data);
	}
	public static void warn(Throwable ex, String msg, Object ... data) {
		log(Level.WARNING, ex, msg, data);
	}
	
	public static void error(String msg, Object ... data) {
		log(Level.SEVERE, null, msg, data);
	}
	public static void error(Throwable ex, String msg, Object ... data) {
		log(Level.SEVERE, ex, msg, data);
	}
	
//	public static void fatal(String msg, Object ... data) {
//		log(Level.SEVERE, null, msg, data);
//	}
//	public static void fatal(Throwable ex, String msg, Object ... data) {
//		log(Level.SEVERE, ex, msg, data);
//	}
}
