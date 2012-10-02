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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mpaf.Logger;
import mpaf.auth.User;
import mpaf.exceptions.ServiceException;

public class Logout extends BaseServlet {
	private static final long serialVersionUID = 4125849575239939124L;

	@Override
	protected void doServiceGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, ServiceException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			User u = (User) session.getAttribute("user");
			session.invalidate();
			if (u != null)
				Logger.info(this.getClass(), "User " + u.getName()
						+ " logged out.");
		}
		sendSuccess(resp);
	}
}
