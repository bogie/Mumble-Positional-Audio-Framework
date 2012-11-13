/*******************************************************************************
 * This file is part of MPAF.
 * 
 * MPAF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MPAF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with MPAF.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package mpaf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;

public class Logger {

	public static final String format = "dd.MM.yyyy HH:mm:ss";
	
	private static void logToFile(String msg) {
		long uptime = ManagementFactory.getRuntimeMXBean().getStartTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		String timestamp = sdf.format(uptime);
		Path path = Paths.get("mpaf-"+timestamp+".log");
		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,StandardOpenOption.APPEND,StandardOpenOption.CREATE)) {
			writer.write(msg);
			writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void debug(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		String outmsg = timestamp + " DEBUG " + clazz.getSimpleName() + ": " + msg;
		System.out.println(outmsg);
		logToFile(outmsg);
	}

	@SuppressWarnings("rawtypes")
	public static void info(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		String outmsg = timestamp + " INFO " + clazz.getSimpleName() + ": " + msg;
		System.out.println(outmsg);
		logToFile(outmsg);
	}

	@SuppressWarnings("rawtypes")
	public static void error(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		String outmsg = timestamp + " ERROR " + clazz.getSimpleName() + ": " + msg;
		System.out.println(outmsg);
		logToFile(outmsg);
	}

	@SuppressWarnings("rawtypes")
	public static void fatal(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		String outmsg = timestamp + " FATAL " + clazz.getSimpleName() + ": " + msg;
		System.out.println(outmsg);
		logToFile(outmsg);
	}
}
