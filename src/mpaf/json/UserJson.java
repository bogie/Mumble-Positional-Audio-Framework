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

import mpaf.auth.User;

public class UserJson {
	@SuppressWarnings("unused")
	private int id;
	@SuppressWarnings("unused")
	private String name;
	@SuppressWarnings("unused")
	private String email;
	@SuppressWarnings("unused")
	private int permissionlevel;

	public UserJson(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.permissionlevel = user.getPermissionlvl();
	}
}
