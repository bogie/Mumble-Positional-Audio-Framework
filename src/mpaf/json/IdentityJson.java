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

public class IdentityJson {
	private int squad;
	private boolean is_leader;
	private String team;
	
	public IdentityJson() {
		// TODO Auto-generated constructor stub
	}
	
	public int getSquad() {
		return this.squad;
	}
	
	public boolean is_leader() {
		return this.is_leader;
	}
	
	public String getTeam() {
		return this.team;
	}
}
