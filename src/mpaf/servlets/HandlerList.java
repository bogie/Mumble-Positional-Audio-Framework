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
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mpaf.Logger;
import mpaf.ServerConfig;
import mpaf.exceptions.ServiceException;
import mpaf.games.DefaultHandler;
import mpaf.games.HandlerType;
import mpaf.ice.IceModel;
import mpaf.json.HandlerJson;
import mpaf.json.HandlerListJson;

public class HandlerList extends BaseServlet {
	private static final long serialVersionUID = 9214867622617584113L;

	@Override
	protected void doServiceGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, ServiceException {
		if (req.getParameter("sid") == null
				|| req.getParameter("sid").isEmpty()) {
			sendError(ErrorCode.REQUIRED_FIELD_MISSING, resp);
			return;
		}
		int sid;
		try {
			sid = Integer.parseInt(req.getParameter("sid"));
		} catch (NumberFormatException e) {
			sendError(ErrorCode.REQUIRED_FIELD_INVALID, resp);
			return;
		}
		IceModel iceM = (IceModel) this.getServletContext().getAttribute(
				"iceModel");
		if (iceM == null) {
			Logger.error(this.getClass(),
					"Could not find IceModel! Skipping request.");
			return;
		}
		ServerConfig server = iceM.getServers().get(sid);
		ArrayList<HandlerJson> jsonhandlers = new ArrayList<HandlerJson>();
		for (Entry<HandlerType, DefaultHandler> handlerSet : server.getCallback()
				.getHandlers().entrySet()) {
			jsonhandlers.add(new HandlerJson(handlerSet.getKey(), true,
					handlerSet.getValue().getGameChannel()));
		}
		send(new HandlerListJson(jsonhandlers), resp);
	}
}
