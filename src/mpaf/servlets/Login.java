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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mpaf.Logger;
import mpaf.auth.User;
import mpaf.exceptions.ServiceException;

public class Login extends BaseServlet {
	private static final long serialVersionUID = -8073664333820127158L;

	@Override
	protected void doServicePost(HttpServletRequest req,
			HttpServletResponse resp) throws IOException, ServiceException {
		User u = checkCredentials(req.getParameter("login_name"),
				req.getParameter("login_pass"));
		// If credentials were incorrect
		if (u == null) {
			sendError(ErrorCode.LOGIN_WRONG_CREDENTIALS, resp);
			return;
		}
		// Do the actual login
		// Get the session, create one if it doesn't exist
		HttpSession session = req.getSession(true);
		session.setAttribute("user", u);
		Logger.info(this.getClass(), "User " + u.getName() + " logged in.");
		sendSuccess(resp);
	}

	private User checkCredentials(String user, String pass)
			throws ServiceException {
		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT id, email, permissionlevel FROM users WHERE name=? AND password=?");
			stmt.setString(1, user);
			stmt.setString(2, pass);
			ResultSet res = stmt.executeQuery();
			// If the user with that password has been found, create user object
			// with permission
			if (res.next()) {
				return new User(res.getInt(1), user, res.getString(2),
						res.getInt(3));
			}
			// Or not...
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServiceException(ErrorCode.INTERNAL_ERROR);
		}
	}
}
