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

public class SqlHandler {

	private String dbuser;
	private String dbpass;
	private String dbhost;
	private String dbname;
	private String dbport;

	// private LinkedList<Connection> connections = new
	// LinkedList<Connection>();

	public synchronized Connection getConnection(String dbname)
			throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn = DriverManager.getConnection("jdbc:mysql://"
				+ this.dbhost + ":" + this.dbport + "/" + this.dbname
				+ "?autoReconnect=true", this.dbuser, this.dbpass);
		// this.connections.add(conn);
		return conn;
	}

	public Connection getConnection() throws SQLException {
		return this.getConnection(this.dbname);
	}

	public String getDbuser() {
		return this.dbuser;
	}

	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}

	public String getDbpass() {
		return this.dbpass;
	}

	public void setDbpass(String dbpass) {
		this.dbpass = dbpass;
	}

	public String getDbhost() {
		return this.dbhost;
	}

	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}

	public String getDbname() {
		return this.dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getDbport() {
		return dbport;
	}

	public void setDbport(String dbport) {
		this.dbport = dbport;
	}
}
