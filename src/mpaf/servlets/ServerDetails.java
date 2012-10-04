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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mpaf.Logger;
import mpaf.ServerConfig;
import mpaf.ice.ChannelHandler;
import mpaf.ice.IceModel;
import mpaf.json.ChannelJson;
import mpaf.json.ServerDetailsJson;
import mpaf.json.ServerJson;
import Murmur.Channel;
import Murmur.InvalidSecretException;
import Murmur.ServerBootedException;

import com.google.gson.Gson;

public class ServerDetails extends BaseServlet {
	private static final long serialVersionUID = -1347748214382340423L;

	public ServerDetails() {
		this.gson = new Gson();
	}

	@Override
	protected void doServiceGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		IceModel iceM = (IceModel) this.getServletContext().getAttribute(
				"iceModel");
		if (iceM == null) {
			Logger.error(this.getClass(),
					"Could not find IceModel! Skipping request.");
			return;
		}
		ArrayList<ServerJson> jsonservers = new ArrayList<ServerJson>();
		HashMap<Integer, ServerConfig> servers = iceM.getServers();
		ChannelHandler channelH = new ChannelHandler(iceM);
		for (ServerConfig server : servers.values()) {
			Map<Integer, Channel> channels = null;
			try {
				channels = channelH
						.getChannels(Integer.parseInt(server.getId()));
			} catch (InvalidSecretException | ServerBootedException
					| NumberFormatException e) {
				e.printStackTrace();
			}
			ArrayList<ChannelJson> jsonchannels = new ArrayList<ChannelJson>();
			for (Channel chan : channels.values()) {
				jsonchannels.add(new ChannelJson(chan));
			}
			jsonservers.add(new ServerJson(server, jsonchannels));
		}
		send(new ServerDetailsJson(jsonservers), resp);
	}
}
