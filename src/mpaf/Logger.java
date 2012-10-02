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

import java.text.SimpleDateFormat;

public class Logger {

	public static final String format = "dd.MM.yyyy HH:mm:ss";

	@SuppressWarnings("rawtypes")
	public static void debug(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		System.out.println(timestamp + " DEBUG " + clazz.getSimpleName() + ": "
				+ msg);
	}

	@SuppressWarnings("rawtypes")
	public static void info(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		System.out.println(timestamp + " INFO " + clazz.getSimpleName() + ": "
				+ msg);
	}

	@SuppressWarnings("rawtypes")
	public static void error(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		System.out.println(timestamp + " ERROR " + clazz.getSimpleName() + ": "
				+ msg);
	}

	@SuppressWarnings("rawtypes")
	public static void fatal(Class clazz, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timestamp = sdf.format(System.currentTimeMillis());
		System.out.println(timestamp + " FATAL " + clazz.getSimpleName() + ": "
				+ msg);
	}
}
