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
package mpaf.json;

import java.util.Collection;

import mpaf.ServerConfig;

public class ServerJson {

	@SuppressWarnings("unused")
	private int id;
	@SuppressWarnings("unused")
	private String registername;
	@SuppressWarnings("unused")
	private String host;
	@SuppressWarnings("unused")
	private int port;
	@SuppressWarnings("unused")
	private int bandwidth;
	@SuppressWarnings("unused")
	private Collection<ChannelJson> channels;

	public ServerJson(ServerConfig server, Collection<ChannelJson> channels) {
		this.id = Integer.parseInt(server.getId());
		this.registername = server.getRegistername();
		this.host = server.getHost();
		this.port = Integer.parseInt(server.getPort());
		this.bandwidth = Integer.parseInt(server.getBandwidth());
		this.channels = channels;
	}
}
