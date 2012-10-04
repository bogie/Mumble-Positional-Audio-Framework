package mpaf.json;

import Murmur.Channel;

public class ChannelJson {

	@SuppressWarnings("unused")
	private int id;
	@SuppressWarnings("unused")
	private String name;
	@SuppressWarnings("unused")
	private int parent;
	@SuppressWarnings("unused")
	private int[] links;
	@SuppressWarnings("unused")
	private String description;
	@SuppressWarnings("unused")
	private boolean temporary;
	@SuppressWarnings("unused")
	private int position;

	public ChannelJson(Channel channel) {
		this.id = channel.id;
		this.name = channel.name;
		this.parent = channel.parent;
		this.links = channel.links;
		this.description = channel.description;
		this.temporary = channel.temporary;
		this.position = channel.position;
	}
}
