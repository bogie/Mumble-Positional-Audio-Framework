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
package mpaf.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mpaf.Logger;

public class SqlightHandler {

	private Connection connection;

	public SqlightHandler() throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		try {
			this.connection = DriverManager
					.getConnection("jdbc:sqlite:mpaf.db");
		} catch (SQLException e) {
			Logger.fatal(this.getClass(),
					"Could not open connection to mpaf.db");
		}
	}

	public Connection getConnection() {
		return this.connection;
	}
}
