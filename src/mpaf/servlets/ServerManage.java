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

import mpaf.Logger;
import mpaf.ServerConfig;
import mpaf.exceptions.ServiceException;
import mpaf.games.Battlefield3Handler;
import mpaf.games.DefaultHandler;
import mpaf.ice.IceModel;

import Murmur.InvalidChannelException;
import Murmur.InvalidSecretException;
import Murmur.ServerBootedException;

import com.google.gson.Gson;

public class ServerManage extends BaseServlet {
	private static final long serialVersionUID = -6206677718183577362L;

	public ServerManage() {
		this.gson = new Gson();
	}
	
	@Override
	protected void doServicePost(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException,
			ServiceException {
		Logger.debug(this.getClass(),"Got new servermanage POST");
		IceModel iceM = (IceModel) this.getServletContext().getAttribute(
				"iceModel");
		if (iceM == null) {
			Logger.error(this.getClass(),
					"Could not find IceModel! Skipping request.");
			return;
		}
		if(req.getParameter("serverId") == null
				|| req.getParameter("handlerName") == null)
		{
			Logger.debug(this.getClass(),"serverId or handlerName is empty");
			sendError(ErrorCode.HANDLER_INVALID_INFORMATION, resp);
			return;
		}
		
		// generic parameters
		int serverId = Integer.parseInt(req.getParameter("serverId"));
		String handlerName = req.getParameter("handlerName");
		
		ServerConfig sc = iceM.getServers().get(serverId);
		DefaultHandler handler = sc.getCallback().getHandlers().get(handlerName);
		if(handler != null)
		{
			// Handler exists, either set new gameChannelId or activate/deactivate handler
			Logger.debug(this.getClass(),"Handler is "+handler);
			try {
				if(req.getParameter("gameChannelId") != null)
				{
					// defining new gameChannel
					int gameChannelId = Integer.parseInt(req.getParameter("gameChannelId"));
					Logger.debug(this.getClass(),"Set new GameChannel: "+gameChannelId+" for game: "+handlerName);
					handler.setGameChannel(gameChannelId);
					sendSuccess(resp);
					return;
				} else if(req.getParameter("activate") != null)
				{
					// de-/activating handler
					Logger.debug(this.getClass(),"Toggled active status");
					if(handler.isActive())
						handler.deactivate();
					else
						handler.activate();
					sendSuccess(resp);
					return;
				} else {
					Logger.debug(this.getClass(),"What did you expect me to do?");
					sendError(ErrorCode.HANDLER_INVALID_INFORMATION, resp);
					return;
				}
			} catch (InvalidSecretException | ServerBootedException
					| InvalidChannelException e) {
				e.printStackTrace();
				sendError(ErrorCode.ICE_GENERIC_ERROR, resp);
				return;
			}
		} else {
			if(req.getParameter("gameChannelId") != null)
			{
				int gameChannelId = Integer.parseInt(req.getParameter("gameChannelId"));
				Logger.debug(this.getClass(),"Trying to create a new GameHandler gamechannelid: "+gameChannelId+" handlername: "+handlerName);
				try {
					if(createGameHandler(serverId, handlerName, gameChannelId))
						sendSuccess(resp);
					else
						sendError(ErrorCode.HANDLER_INVALID_INFORMATION, resp);
					return;
				} catch (InvalidChannelException | InvalidSecretException | ServerBootedException e) {
					e.printStackTrace();
					sendError(ErrorCode.ICE_GENERIC_ERROR, resp);
					return;
				}
			} else {
				sendError(ErrorCode.HANDLER_INVALID_INFORMATION, resp);
				return;
			}
		}
	}
	
	private boolean createGameHandler(int serverId, String handlerName, int gameChannelId) throws InvalidSecretException, ServerBootedException, InvalidChannelException {
		IceModel iceM = (IceModel) this.getServletContext().getAttribute(
				"iceModel");
		if (iceM == null) {
			Logger.error(this.getClass(),
					"Could not find IceModel! Skipping request.");
			return false;
		}
		Murmur.ServerPrx server = iceM.getMeta().getServer(serverId);
		ServerConfig sc = iceM.getServers().get(serverId);
		DefaultHandler handler = null;
		if(handlerName.equalsIgnoreCase("Battlefield 3"))
		{
			handler = new Battlefield3Handler(server);
			Logger.debug(this.getClass(),"New BF3Handler is: "+handler);
		} else {
			Logger.debug(this.getClass(),"Tried to create unsupported GameHandler");
			return false;
		}
		handler.setGameChannel(gameChannelId);
		sc.getCallback().getHandlers().put(handlerName, handler);
		return true;
	}
}
