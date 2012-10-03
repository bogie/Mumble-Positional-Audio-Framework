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

import mpaf.ice.IceController;
import mpaf.ice.IceModel;
import mpaf.servlets.DefaultCacheServlet;
import mpaf.servlets.Login;
import mpaf.servlets.Logout;
import mpaf.servlets.ServerDetails;
import mpaf.servlets.UserCreate;
import mpaf.servlets.UserInfo;
import mpaf.servlets.UserList;
import mpaf.sql.SqlightHandler;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
	public static void main(String[] args) {

		// Apache commons configuration
		CompositeConfiguration config = new CompositeConfiguration();
		try {

			XMLConfiguration user = new XMLConfiguration(
					"mpaf.properties.user.xml");
			XMLConfiguration defaults = new XMLConfiguration(
					"mpaf.properties.default.xml");

			// careful configuration is read from top to bottom if you want a
			// config to overwrite the user config, add it as first element
			// also make it optional to load, check if the file exists and THEN
			// load it!
			if (user != null)
				config.addConfiguration(user);
			config.addConfiguration(defaults);
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		}

		IceModel iceM = new IceModel(config);
		IceController iceC = new IceController(iceM);

		Server server = new Server();

		ConsoleParser parser = new ConsoleParser(iceC, iceM, server);
		new Thread(parser).start();

		// will be called once a shutdown event is thrown(like ctrl+c or sigkill
		// etc.)
		ShutdownThread shutdown = new ShutdownThread(iceC);
		Runtime.getRuntime().addShutdownHook(new Thread(shutdown));

		SocketConnector connector = new SocketConnector();
		connector.setPort(config.getInt("jetty.ports.http", 10001));
		server.setConnectors(new Connector[] { connector });

		ServletContextHandler servletC = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		servletC.setContextPath("/");
		try {
			servletC.setAttribute("sqlighthandler", new SqlightHandler());
		} catch (ClassNotFoundException e1) {
			System.out.println("FATAL: Could not connect to mpaf.db database!");
		}
		servletC.setAttribute("iceController", iceC);
		servletC.setAttribute("iceModel", iceM);
		// To add a servlet:
		ServletHolder holder = new ServletHolder(new DefaultCacheServlet());
		holder.setInitParameter("cacheControl", "max-age=3600,public");
		holder.setInitParameter("resourceBase", "web");
		servletC.addServlet(holder, "/");
		servletC.addServlet(new ServletHolder(new ServerDetails()),
				"/serverdetails");
		servletC.addServlet(new ServletHolder(new Login()), "/login");
		servletC.addServlet(new ServletHolder(new Logout()), "/logout");
		servletC.addServlet(new ServletHolder(new UserCreate()), "/usercreate");
		servletC.addServlet(new ServletHolder(new UserInfo()), "/userinfo");
		servletC.addServlet(new ServletHolder(new UserList()), "/userlist");

		server.setHandler(servletC);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
