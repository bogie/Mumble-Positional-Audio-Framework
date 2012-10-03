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
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mpaf.auth.User;
import mpaf.json.UserJson;
import mpaf.json.UserListJson;

public class UserList extends BaseServlet {
	private static final long serialVersionUID = -6450452256952055336L;

	@Override
	protected void doServicePost(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		PreparedStatement stmt;
		try {
			if (req.getParameter("filter") != null
					&& !req.getParameter("filter").isEmpty()) {
				stmt = conn
						.prepareStatement("SELECT id, name, email, permissionlevel FROM users WHERE name LIKE ?");
				stmt.setString(1, "%" + req.getParameter("filter") + "%");
			} else {
				stmt = conn
						.prepareStatement("SELECT id, name, email, permissionlevel FROM users");
			}
			ResultSet res = stmt.executeQuery();
			ArrayList<UserJson> userarray = new ArrayList<UserJson>();
			while (res.next()) {
				UserJson u = new UserJson(new User(res.getInt(1),
						res.getString(2), res.getString(3), res.getInt(4)));
				userarray.add(u);
			}
			send(new UserListJson(userarray), resp);
		} catch (SQLException e) {
			e.printStackTrace();
			sendError(ErrorCode.INTERNAL_ERROR, resp);
		}
	}
}
