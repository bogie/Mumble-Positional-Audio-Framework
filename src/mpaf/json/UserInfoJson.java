package mpaf.json;

import mpaf.auth.User;

public class UserInfoJson {

	@SuppressWarnings("unused")
	private int id;
	@SuppressWarnings("unused")
	private String name;
	@SuppressWarnings("unused")
	private int permissionlvl;

	public UserInfoJson(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.permissionlvl = user.getPermissionlvl();
	}
}
