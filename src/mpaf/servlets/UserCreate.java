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

import mpaf.Logger;
import mpaf.exceptions.ServiceException;

public class UserCreate extends BaseServlet {
	private static final long serialVersionUID = -3262246528104948232L;

	protected void doServicePost(HttpServletRequest req,
			HttpServletResponse resp) throws IOException, ServiceException {
		if (req.getParameter("create_name") == null
				|| req.getParameter("create_pass") == null
				|| req.getParameter("create_email") == null
				|| req.getParameter("create_permlvl") == null) {
			sendError(ErrorCode.CREATE_INVALID_CREDENTIALS, resp);
			return;
		}
		try {
			createUser(req.getParameter("create_name"),
					req.getParameter("create_pass"),
					req.getParameter("create_email"),
					Integer.parseInt(req.getParameter("create_permlvl")));
		} catch (NumberFormatException e) {
			sendError(ErrorCode.CREATE_INVALID_CREDENTIALS, resp);
			return;
		}
		Logger.info(this.getClass(),
				"Created user " + req.getParameter("create_name"));
		sendSuccess(resp);
	}

	private void createUser(String user, String pass, String email, int permlvl)
			throws ServiceException {
		try {
			PreparedStatement stmtcheck = conn
					.prepareStatement("SELECT id FROM users WHERE name=?");
			stmtcheck.setString(1, user);
			ResultSet rescheck = stmtcheck.executeQuery();
			if (rescheck.next())
				throw new ServiceException(ErrorCode.CREATE_NAME_TAKEN);
			PreparedStatement stmtcreate = conn
					.prepareStatement("INSERT INTO users (name,password, email, permissionlevel) VALUES (?,?,?,?)");
			stmtcreate.setString(1, user);
			stmtcreate.setString(2, pass);
			stmtcreate.setString(3, email);
			stmtcreate.setInt(4, permlvl);
			stmtcreate.execute();
			stmtcreate.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServiceException(ErrorCode.INTERNAL_ERROR);
		}
	}
}
