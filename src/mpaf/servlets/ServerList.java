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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mpaf.Logger;
import mpaf.ServerConfig;
import mpaf.ice.IceModel;
import mpaf.json.ServerJson;
import mpaf.json.ServerListJson;

import com.google.gson.Gson;

public class ServerList extends BaseServlet {
	private static final long serialVersionUID = -1347748214382340423L;

	public ServerList() {
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
		for (ServerConfig server : servers.values()) {
			jsonservers.add(new ServerJson(server));
		}
		send(new ServerListJson(jsonservers), resp);
	}
}
