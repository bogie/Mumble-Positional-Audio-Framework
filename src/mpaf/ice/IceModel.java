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

import java.util.HashMap;

import mpaf.ServerConfig;

import org.apache.commons.configuration.CompositeConfiguration;

public class IceModel {
	@SuppressWarnings("unused")
	private CompositeConfiguration config;
	private String secret = "eivair5G";
	private String serverIp = "192.168.122.102";
	private String clientIp = "192.168.122.102";
	private String icePort = "6502";
	private Ice.Communicator communicator;
	private Murmur.MetaPrx meta;
	private HashMap<Integer, ServerConfig> servers = new HashMap<Integer, ServerConfig>();

	public IceModel() {
		// just in case :P
	}

	public IceModel(CompositeConfiguration config) {
		this.config = config;
		secret = config.getString("ice.secret");
		serverIp = config.getString("ice.hosts.remote");
		clientIp = config.getString("ice.hosts.local");
		icePort = config.getString("ice.port");
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getIcePort() {
		return icePort;
	}

	public void setIcePort(String icePort) {
		this.icePort = icePort;
	}

	public Ice.Communicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(Ice.Communicator communicator) {
		this.communicator = communicator;
	}

	public Murmur.MetaPrx getMeta() {
		return meta;
	}

	public void setMeta(Murmur.MetaPrx meta) {
		this.meta = meta;
	}

	public HashMap<Integer, ServerConfig> getServers() {
		return servers;
	}

	public void setServers(HashMap<Integer, ServerConfig> servers) {
		this.servers = servers;
	}
}
