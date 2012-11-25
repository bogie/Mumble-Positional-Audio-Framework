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
package mpaf.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mpaf.Logger;
import mpaf.auth.PermissionLevel;
import mpaf.auth.User;
import mpaf.exceptions.ServiceException;
import mpaf.json.ResultJson;
import mpaf.sql.SqlHandler;

import com.google.gson.Gson;

/**
 * The Class BaseServlet is a modification of the HttpServlet class that
 * implements centralized exception handling and privilege checking for the POST
 * method.
 */
public abstract class BaseServlet extends HttpServlet {

	protected static final long serialVersionUID = -1992787881861353752L;

	private static final String METHOD_DELETE = "DELETE";
	private static final String METHOD_HEAD = "HEAD";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_OPTIONS = "OPTIONS";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_TRACE = "TRACE";

	private static final String HEADER_IFMODSINCE = "If-Modified-Since";
	private static final String HEADER_LASTMOD = "Last-Modified";

	/** The Constant LSTRING_FILE. */
	private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
	private static ResourceBundle lStrings = ResourceBundle
			.getBundle(LSTRING_FILE);

	protected Gson gson;

	protected Connection conn;
	protected int minpermissionlvl = 0;

	private static final String REQUEST_ENCODING = "UTF-8";
	private static final String RESPONSE_ENCODING = REQUEST_ENCODING;

	private static final String JSON_CONTENT_TYPE = "application/json";
	protected String response_content_type = JSON_CONTENT_TYPE; // default

	/**
	 * Initiates the servlet, especially the database connection.
	 */
	@Override
	public void init() {
		this.gson = new Gson();
		this.conn = ((SqlHandler) this.getServletContext().getAttribute(
				"sqlhandler")).getConnection();
		loadPrivileges();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.initEncoding(req, resp);
		// If the database with permissions could not be reached, deny
		// everything
		if (minpermissionlvl < PermissionLevel.ANON.getLevel()) {
			sendError(ErrorCode.INTERNAL_ERROR, resp);
		}
		// If this servlet requires at least user rights
		if (minpermissionlvl > PermissionLevel.INACTIVE.getLevel()) {
			// If there is no session, the user did not submit a cookie
			HttpSession session = req.getSession(false);
			if (session == null) {
				sendError(ErrorCode.RIGHT_NOT_LOGGED_IN, resp);
				return;
			}
			User user = (User) session.getAttribute("user");
			if (user == null) {
				sendError(ErrorCode.RIGHT_NOT_LOGGED_IN, resp);
				return;
			}
			// If the permissionlevel of the logged-in user is too low
			if (user.getPermissionlvl() < minpermissionlvl) {
				sendError(ErrorCode.RIGHT_INSUFFICIENT_PERMISSION, resp);
				return;
			}
		}
		String method = req.getMethod();

		if (method.equals(METHOD_GET)) {
			long lastModified = getLastModified(req);
			try {
				if (lastModified == -1) {
					// No last-modified? Just do the get already
					doServiceGet(req, resp);
				} else {
					long ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
					if (ifModifiedSince < (lastModified / 1000 * 1000)) {
						// If the servlet mod time is later, call doGet()
						// Round down to the nearest second for a proper compare
						// A ifModifiedSince of -1 will always be less
						maybeSetLastModified(resp, lastModified);

						doServiceGet(req, resp);
					} else {
						resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					}
				}
			} catch (ServiceException e) {
				send(new ResultJson(false, e.getError().getCode()), resp);
			}

			// behaviour like in HttpServlet
		} else if (method.equals(METHOD_HEAD)) {
			long lastModified = getLastModified(req);
			maybeSetLastModified(resp, lastModified);
			doHead(req, resp);

			// here begins the custom behaviour for the BaseServlet class
		} else if (method.equals(METHOD_POST)) {
			try {
				// if exception send error
				doServicePost(req, resp);

			} catch (ServiceException e) {
				send(new ResultJson(false, e.getError().getCode()), resp);
			}

		} else if (method.equals(METHOD_PUT)) {
			doPut(req, resp);

		} else if (method.equals(METHOD_DELETE)) {
			doDelete(req, resp);

		} else if (method.equals(METHOD_OPTIONS)) {
			doOptions(req, resp);

		} else if (method.equals(METHOD_TRACE)) {
			doTrace(req, resp);

		} else {
			//
			// Note that this means NO servlet supports whatever
			// method was requested, anywhere on this server.
			//

			String errMsg = lStrings.getString("http.method_not_implemented");
			Object[] errArgs = new Object[1];
			errArgs[0] = method;
			errMsg = MessageFormat.format(errMsg, errArgs);

			resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
		}
	}

	/**
	 * Inits the encoding and sets content type.
	 * 
	 * @param req
	 *            the request
	 * @param resp
	 *            the response
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	protected void initEncoding(HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException {
		req.setCharacterEncoding(BaseServlet.REQUEST_ENCODING);
		resp.setCharacterEncoding(BaseServlet.RESPONSE_ENCODING);

		resp.setContentType(BaseServlet.JSON_CONTENT_TYPE);
	}

	/*
	 * Sets the Last-Modified entity header field, if it has not already been
	 * set and if the value is meaningful. Called before doGet, to ensure that
	 * headers are set before response data is written. A subclass might have
	 * set this header already, so we check.
	 */
	private void maybeSetLastModified(HttpServletResponse resp,
			long lastModified) {
		if (resp.containsHeader(HEADER_LASTMOD))
			return;
		if (lastModified >= 0)
			resp.setDateHeader(HEADER_LASTMOD, lastModified);
	}

	/**
	 * Send an object as json.
	 * 
	 * @param o
	 *            the object to serialize
	 * @param resp
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void send(Object o, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		String msg = gson.toJson(o);
		out.print(msg);
	}

	protected void sendSuccess(HttpServletResponse resp) throws IOException {
		send(new ResultJson(true, ErrorCode.SUCCESS.getCode()), resp);
	}

	protected void sendError(ErrorCode error, HttpServletResponse resp)
			throws IOException {
		send(new ResultJson(false, error.getCode()), resp);
	}

	/**
	 * This replaces the doPost method of the HttpServlet class.
	 * 
	 * @param req
	 *            the request
	 * @param resp
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServiceException
	 *             the service exception
	 */
	protected void doServicePost(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException,
			ServiceException {
		PrintWriter out = resp.getWriter();
		out.print("I POST you :)");
	}

	/**
	 * This replaces the doGet method of the HttpServlet class.
	 * 
	 * @param req
	 *            the request
	 * @param resp
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServiceException
	 *             the service exception
	 */
	protected void doServiceGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, ServiceException {
		PrintWriter out = resp.getWriter();
		out.print("I GET you :3");
	}

	/**
	 * Destroys the servlet, especially the database connection.
	 */
	@Override
	public void destroy() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadPrivileges() {
		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT minpermissionlevel FROM servlets WHERE name=?");
			stmt.setString(1, this.getClass().getSimpleName());
			ResultSet res = stmt.executeQuery();
			if (res.next()) {
				minpermissionlvl = res.getInt(1);
			} else {
				Logger.info(this.getClass(),
						"Could not find permission level for servlet "
								+ this.getClass().getSimpleName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			minpermissionlvl = -1;
		}
	}
}
