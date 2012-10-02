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
package mpaf;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import mpaf.ice.ServerCallbackI;
import Murmur.InvalidSecretException;
import Murmur.ServerPrx;

public class ServerConfig {

	private String id;
	private String port;
	private String users;
	private String rememberchannel;
	private String channelname;
	private String host;
	private String obfuscate;
	private String password;
	private String welcometext;
	private String bandwidth;
	private String registerpassword;
	private String username;
	private String certificate;
	private String certrequired;
	private String textmessagelength;
	private String registerlocation;
	private String registername;
	private String allowhtml;
	private String defaultchannel;
	private String bonjour;
	private String registerhostname;
	private String registerurl;
	private String timeout;
	private String key;
	private ServerCallbackI callback;

	public ServerConfig(ServerPrx server, Map<String, String> defaultConfig) {
		try {
			this.id = String.valueOf(server.id());
			for (Entry<String, String> conf : defaultConfig.entrySet()) {
				@SuppressWarnings("rawtypes")
				Class confclass = this.getClass();
				Field f = confclass.getDeclaredField(conf.getKey()
						.toLowerCase());
				f.set(this, conf.getValue());

			}
			for (Entry<String, String> conf : server.getAllConf().entrySet()) {
				@SuppressWarnings("rawtypes")
				Class confclass = this.getClass();
				Field f = confclass.getDeclaredField(conf.getKey()
						.toLowerCase());
				f.set(this, conf.getValue());
			}
		} catch (InvalidSecretException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			Logger.info(this.getClass(), "Found key " + e.getMessage()
					+ " without field to set value to.");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public String getRememberchannel() {
		return rememberchannel;
	}

	public void setRememberchannel(String rememberchannel) {
		this.rememberchannel = rememberchannel;
	}

	public String getChannelname() {
		return channelname;
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getObfuscate() {
		return obfuscate;
	}

	public void setObfuscate(String obfuscate) {
		this.obfuscate = obfuscate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getWelcometext() {
		return welcometext;
	}

	public void setWelcometext(String welcometext) {
		this.welcometext = welcometext;
	}

	public String getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}

	public String getRegisterpassword() {
		return registerpassword;
	}

	public void setRegisterpassword(String registerpassword) {
		this.registerpassword = registerpassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getCertrequired() {
		return certrequired;
	}

	public void setCertrequired(String certrequired) {
		this.certrequired = certrequired;
	}

	public String getTextmessagelength() {
		return textmessagelength;
	}

	public void setTextmessagelength(String textmessagelength) {
		this.textmessagelength = textmessagelength;
	}

	public String getRegisterlocation() {
		return registerlocation;
	}

	public void setRegisterlocation(String registerlocation) {
		this.registerlocation = registerlocation;
	}

	public String getRegistername() {
		return registername;
	}

	public void setRegistername(String registername) {
		this.registername = registername;
	}

	public String getAllowhtml() {
		return allowhtml;
	}

	public void setAllowhtml(String allowhtml) {
		this.allowhtml = allowhtml;
	}

	public String getDefaultchannel() {
		return defaultchannel;
	}

	public void setDefaultchannel(String defaultchannel) {
		this.defaultchannel = defaultchannel;
	}

	public String getBonjour() {
		return bonjour;
	}

	public void setBonjour(String bonjour) {
		this.bonjour = bonjour;
	}

	public String getRegisterhostname() {
		return registerhostname;
	}

	public void setRegisterhostname(String registerhostname) {
		this.registerhostname = registerhostname;
	}

	public String getRegisterurl() {
		return registerurl;
	}

	public void setRegisterurl(String registerurl) {
		this.registerurl = registerurl;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ServerCallbackI getCallback() {
		return callback;
	}

	public void setCallback(ServerCallbackI callback) {
		this.callback = callback;
	}
}
