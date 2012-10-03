package mpaf.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mpaf.auth.User;
import mpaf.exceptions.ServiceException;
import mpaf.json.UserInfoJson;

public class UserInfo extends BaseServlet {
	private static final long serialVersionUID = 1270988903285654668L;

	@Override
	protected void doServiceGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, ServiceException {
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
		send(new UserInfoJson(user), resp);
	}
}
