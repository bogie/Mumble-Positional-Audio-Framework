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
package mpaf.ice;

import java.util.Map;

import Murmur.Channel;
import Murmur.InvalidChannelException;
import Murmur.InvalidSecretException;
import Murmur.ServerBootedException;

public class ChannelHandler {
	IceModel im;
	
	public ChannelHandler(IceModel im) {
		this.im = im;
	}
	
	public Map<Integer, Channel> getChannels(Integer serverId) throws InvalidSecretException, ServerBootedException {
		Murmur.ServerPrx server = this.im.getMeta().getServer(serverId);
		if(server != null)
			return server.getChannels();
		else
			return null;
	}
	
	public int addChannel(Integer serverId, String name, Integer parent) throws InvalidChannelException, InvalidSecretException, ServerBootedException {
		Murmur.ServerPrx server = this.im.getMeta().getServer(serverId);
		if(server != null)
			return server.addChannel(name, parent);
		else
			return -1;
	}
	
	public void removeChannel(Integer serverId, Integer channelId) throws InvalidChannelException, InvalidSecretException, ServerBootedException {
		Murmur.ServerPrx server = this.im.getMeta().getServer(serverId);
		if(server != null)
			server.removeChannel(channelId);
	}
}
