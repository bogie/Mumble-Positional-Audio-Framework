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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mpaf.Logger;
import mpaf.exceptions.ServiceException;
import mpaf.ice.ChannelHandler;
import mpaf.ice.IceModel;
import mpaf.json.ChannelJson;
import mpaf.json.ChannelListJson;
import Murmur.Channel;
import Murmur.InvalidSecretException;
import Murmur.ServerBootedException;

public class ChannelList extends BaseServlet {
	private static final long serialVersionUID = -5655762771993596266L;

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
		ArrayList<ChannelJson> jsonchannels = new ArrayList<ChannelJson>();
		ChannelHandler channelH = new ChannelHandler(iceM);
		Map<Integer, Channel> channels;
		try {
			channels = channelH.getChannels(sid);
		} catch (InvalidSecretException | ServerBootedException e) {
			e.printStackTrace();
			return;
		}
		for (Channel chan : channels.values()) {
			jsonchannels.add(new ChannelJson(chan));
		}
		send(new ChannelListJson(jsonchannels), resp);
	}
}
