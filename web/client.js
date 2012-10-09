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

var ErrorCode = {
		/**
		 * Given whenever an operation was successful
		 */
		SUCCESS: 0,

		/**
		 * There was an internal error. Tough luck.
		 */
		INTERNAL_ERROR: 1,

		/**
		 * The given login credentials were wrong
		 */
		LOGIN_WRONG_CREDENTIALS: 2,

		/**
		 * The given user create credentials were invalid
		 */
		CREATE_INVALID_CREDENTIALS: 3,

		/**
		 * The given name has already been taken
		 */
		CREATE_NAME_TAKEN: 4,

		/**
		 * The attempting user is not logged in
		 */
		RIGHT_NOT_LOGGED_IN: 5,

		/**
		 * The rights of the attempting user are too low
		 */
		RIGHT_INSUFFICIENT_PERMISSION: 6,

		/**
		 * The given handler information is invalid
		 */
		HANDLER_INVALID_INFORMATION: 7,

		/**
		 * Generic ICE Error
		 */
		ICE_GENERIC_ERROR: 8,

		/**
		 * A needed data field was missing from the request
		 */
		REQUIRED_FIELD_MISSING: 9,
		
		/**
		 * A needed data field is invalid in the request
		 */
		REQUIRED_FIELD_INVALID: 10
}

var RESULTBOX_FADE_DELAY = 1000;

var loginState = false;

var user;

function User(id, name, permlvl) {
	this.id = id;
	this.name = name;
	this.permlvl = permlvl;
}

function makeGenericCallback(callback) {
	return function(data) {
		// If there is no data.success field or it is true, nothing needs to be done
		if(data.success == undefined || data.success) {
			callback(data);
		} else {
			// If there was an error
			switch (data.errorcode) {
			// If the error was that we are not logged in, log us out
			case ErrorCode.RIGHT_NOT_LOGGED_IN:
				forceLogout();
			break;
			// Otherwise just call the callback
			default:
				callback(data);
			break;
			}
		}
	}
}

function loadUserInfo() {
	$.get("/userinfo",makeGenericCallback(onUserInfoResponse));
}

function onUserInfoResponse(data) {
	if(data.success == undefined) {
		user = new User(data.id, data.name, data.permissionlvl);
		updateUserInfo();
	} else {
		switch(data.errorcode) {
			default:
				$("#debug").append("<br />UserInfo: Received unhandled error "+data.errorcode);
			break;
		}
	}
}

function doLogin() {
	$("#login_load").show();
	$("#login_button").attr("disabled", "disabled");
	var user = $("#login_user").val();
	var pass = $("#login_pass").val();
	$.post("/login", {login_name: user, login_pass: pass},makeGenericCallback(onLoginResponse));
}

function onLoginResponse(data) {
	var resultbox = $("#login_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success) {
		loginSwitch(true);
		resultbox.text("Login successfull!");
		resultbox.addClass("successbox");
	} else {
		resultbox.addClass("errorbox");
		switch(data.errorcode) {
			case ErrorCode.LOGIN_WRONG_CREDENTIALS:
				resultbox.text("Login failed: Wrong credentials");
			break;
			default:
				resultbox.text("Unknown error number "+data.errorcode);
			break;
		}
	}
	$("#login_button").removeAttr("disabled");
	$("#login_load").hide();
	resultbox.fadeIn().delay(RESULTBOX_FADE_DELAY).fadeOut();
}

function doLogout() {
	$("#logout_load").show();
	$("#logout_button").attr("disabled", "disabled");
	$.get("/logout",makeGenericCallback(onLogoutResponse));
}

function onLogoutResponse(data) {
	var resultbox = $("#logout_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success) {
		forceLogout();
		resultbox.text("Logout successfull!");
		resultbox.addClass("successbox");
	} else {
		switch(data.errorcode) {
			default:
				resultbox.text("Logout: Received unhandled error number "+data.errorcode);
			break;
		}
	}
	$("#logout_button").removeAttr("disabled");
	$("#logout_load").hide();
	resultbox.fadeIn().delay(RESULTBOX_FADE_DELAY).fadeOut();
}

function doUserCreate() {
	$("#usercreate_load").show();
	$("#usercreate_button").attr("disabled", "disabled");
	var user = $("#usercreate_user").val();
	var pass = $("#usercreate_pass").val();
	var email = $("#usercreate_email").val();
	var permlvl = $("#usercreate_permlvl").val();
	$.post("/usercreate", {create_name: user, create_pass: pass, create_email: email, create_permlvl: permlvl},makeGenericCallback(onUserCreateResponse));
}

function onUserCreateResponse(data) {
	var resultbox = $("#usercreate_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success) {
		resultbox.text("Created successfully");
		resultbox.addClass("successbox");
	} else {
		resultbox.addClass("errorbox");
		switch(data.errorcode) {
			case ErrorCode.CREATE_INVALID_CREDENTIALS:
				resultbox.text("Invalid credentials!");
			break;
			case ErrorCode.CREATE_NAME_TAKEN:
				resultbox.text("Name taken!");
			break;
			default:
				resultbox.text("Unknown error number "+data.errorcode);
			break;
		}
	}
	$("#usercreate_button").removeAttr("disabled");
	$("#usercreate_load").hide();
	resultbox.fadeIn().delay(RESULTBOX_FADE_DELAY).fadeOut();
}

function doServerManage() {
	$("#servermanage_load").show();
	$("#servermanage_button").attr("disabled", "disabled");
	var serverid = $("#servermanage_serverid").val();
	var handlername = $("#servermanage_handlername").val();
	var gamechannelid = $("#servermanage_gamechannelid").val();
	var active = $("#servermanage_active").val();
	$.post("/servermanage", {serverId: serverid, handlerName: handlername, gameChannelId: gamechannelid, activate: active},makeGenericCallback(onServerManageResponse));
}

function onServerManageResponse(data) {
	var resultbox = $("#servermanage_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success) {
		resultbox.text("Server updated successfully");
		resultbox.addClass("successbox");
	} else {
		resultbox.addClass("errorbox");
		switch(data.errorcode) {
			case ErrorCode.HANDLER_INVALID_INFORMATION:
				resultbox.text("Invalid information!");
				break;
			default:
				resultbox.text("Unknown error number "+data.errorcode);
				break;
		}
	}
	$("#servermanage_button").removeAttr("disabled");
	$("#servermanage_load").hide();
	resultbox.fadeIn().delay(RESULTBOX_FADE_DELAY).fadeOut();
}

function fetchServerList() {
	$("#serverdetails_load").show();
	$("#serverdetails_button").attr("disabled", "disabled");
	$.get("/serverlist",makeGenericCallback(onServerListResponse));
}

function onServerListResponse(data) {
	var resultbox = $("#serverdetails_result");
	resultbox.removeClass("errorbox successbox");
	$("#serverdetails_serverlist div").remove();
	var servers = data.servers;
	for(sid in data.servers) {
		$("<div>").attr("id","serverdetails_serverlist_server"+servers[sid].id).appendTo("#serverdetails_serverlist");
		var server_details = $("<details>").attr("id","serverdetails_serverlist_server"+servers[sid].id+"_root").appendTo("#serverdetails_serverlist_server"+servers[sid].id);
		var server_summary = $("<summary>").attr("id","serverdetails_serverlist_server"+servers[sid].id+"_root_sum").appendTo(server_details);
		$("<div>").text(servers[sid].registername).appendTo(server_summary);
		$("<div>").text("ID: "+servers[sid].id).appendTo(server_details);
		$("<div>").text("Host: "+servers[sid].host).appendTo(server_details);
		$("<div>").text("Port: "+servers[sid].port).appendTo(server_details);
		$("<div>").text("Bandwidth: "+servers[sid].bandwidth).appendTo(server_details);
		
	}
}

// TODO: Implement channel and handler list in a proper way
/*
function loadServerDetails() {
	$("#serverdetails_load").show();
	$("#serverdetails_button").attr("disabled", "disabled");
	$.get("/serverdetails",makeGenericCallback(onServerDetailsResponse));
}

function onServerDetailsResponse(data) {
	var resultbox = $("#serverdetails_result");
	resultbox.removeClass("errorbox successbox");
	$("#serverdetails_serverlist div").remove();
	// For each server add all the JSON data we receive
	$.each(data.servers,function(server_index, server_item) {
		$("<div>").attr("id","serverdetails_serverlist_server"+server_item.id).appendTo("#serverdetails_serverlist");
		$("<details>").attr("id","serverdetails_serverlist_server"+server_item.id+"_root").appendTo("#serverdetails_serverlist_server"+server_item.id);
		$("<summary>").attr("id","serverdetails_serverlist_server"+server_item.id+"_root_sum").appendTo("#serverdetails_serverlist_server"+server_item.id+"_root");
		$("<div>").text(server_item.registername).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root_sum");
		$("<div>").text("ID: "+server_item.id).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root");
		$("<div>").text("Host: "+server_item.host).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root");
		$("<div>").text("Port: "+server_item.port).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root");
		$("<div>").text("Bandwidth: "+server_item.bandwidth).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root");
		$("<details>").attr("id","serverdetails_serverlist_server"+server_item.id+"_root_channel"+server_item.channels[0].id).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root");
		$("<summary>").text(server_item.channels[0].name).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root_channel"+server_item.channels[0].id);
		for(i=1;i<server_item.channels.length;i++) {
			$("<details>").attr("id","serverdetails_serverlist_server"+server_item.id+"_root_channel"+server_item.channels[i].id).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root_channel"+server_item.channels[i].parent);
			$("<summary>").attr("id","serverdetails_serverlist_server"+server_item.id+"_root_channel_sum"+server_item.channels[i].id).text(server_item.channels[i].name).appendTo("#serverdetails_serverlist_server"+server_item.id+"_root_channel"+server_item.channels[i].id);
		}
		for(i=0;i<server_item.handlers.length;i++) {
			if(server_item.handlers[i].handlerName == "Battlefield 3") {
				$("<img>").attr("src","img/bf3.png").appendTo("#serverdetails_serverlist_server"+server_item.id+"_root_channel_sum"+server_item.handlers[i].gameChannelId);
			}
		}
	});
	resultbox.text("Refreshed...");
	resultbox.addClass("successbox");
	$("#serverdetails_button").removeAttr("disabled");
	$("#serverdetails_load").hide();
	resultbox.fadeIn().delay(RESULTBOX_FADE_DELAY).fadeOut();
}

*/

function onUserManageFilterChange() {
	loadUserList($("#usermanage_filter").val());
}

function fetchUserList(filter) {
	$("#usermanage_load").show();
	$("#usermanage_button").attr("disabled", "disabled");
	if(filter == undefined || filter == "") {
		$.post("/userlist",makeGenericCallback(onUserListResponse));
	} else {
		$.post("/userlist",{filter: filter},makeGenericCallback(onUserListResponse));
	}
}

function onUserListResponse(data) {
	var resultbox = $("#usermanage_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success != undefined) {
		resultbox.addClass("errorbox");
		resultbox.text("Unknown error "+data.errorcode);
		return;
	}
	resultbox.text("Refreshed...");
	resultbox.addClass("successbox");
	$("#usermanage_userlist div").remove();
	$.each(data.users,function(index,item) {
		$("<div>").attr("id","usermanage_user"+data.users[index].id).appendTo("#usermanage_userlist");
		$("<input>").val(data.users[index].name).appendTo("#usermanage_user"+data.users[index].id);
		$("<input>").val(data.users[index].email).appendTo("#usermanage_user"+data.users[index].id);
		$("<input>").val(data.users[index].permissionlevel).appendTo("#usermanage_user"+data.users[index].id);
	});
	$("#usermanage_button").removeAttr("disabled");
	$("#usermanage_load").hide();
	resultbox.fadeIn().delay(RESULTBOX_FADE_DELAY).fadeOut();
}

function updateUserInfo() {
	$("#logout_userinfo").text("Hello "+user.name+"! Permission level: "+user.permlvl);
}

function fetchHTML() {
	$("#_debug").load("debug.html");
	$("#_login").load("login.html");
	$("#_logout").load("logout.html");
	$("#_home").load("home.html");
	$("#_serverdetails").load("serverdetails.html");
	$("#_servermanage").load("servermanage.html");
	$("#_usercreate").load("usercreate.html");
	$("#_usermanage").load("usermanage.html");
}

function loadPage(event) {
	fetchHTML();
	checkLoginStatus();
	updateServerDetails();
	fetchUserList();
	onHashChanged(event);
}

function onHashChanged(event) {
	// Remove leading #
	var hash = location.hash.substring(1);
	// Hide all other content switches
	$(".cswitch").hide();
	// Show the one switched to
	$("#_"+hash).show();
}

function removeCookie() {
	$.removeCookie("JSESSIONID");
}

function forceLogout() {
	removeCookie();
	loginSwitch(false);
}

function checkLoginStatus() {
	$("#debug").append("<br />Checking login status...");
	if($.cookie("JSESSIONID") != null) {
		$("#debug").append("<br />Logged in");
		loginSwitch(true);
	} else {
		$("#debug").append("<br />Not logged in");
		loginSwitch(false);
	}
}

function loginSwitch(login) {
	loginState = login;
	if(loginState) {
		loadUserInfo();
		$(".loggedout").hide();
		$(".loggedin").show();
	} else {
		$(".loggedin").hide();
		$(".loggedout").show();
	}
}
