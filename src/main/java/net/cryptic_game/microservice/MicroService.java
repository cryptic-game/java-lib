package net.cryptic_game.microservice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import net.cryptic_game.microservice.config.Config;
import net.cryptic_game.microservice.config.DefaultConfig;

public abstract class MicroService extends SimpleChannelInboundHandler<String> {

	private static final boolean EPOLL = Epoll.isAvailable();

	private String name;
	private Channel channel;

	public MicroService(String name) {
		this.name = name;
		this.register();
	}

	public String getName() {
		return name;
	}

	private void register() {
		EventLoopGroup group = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

		try {
			Map<String, String> test = new HashMap<String, String>();

			test.put("action", "register");
			test.put("name", this.getName());

			Channel channel = new Bootstrap().group(group)
					.channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
					.handler(new MicroServiceInitializer(this))
					.connect(Config.get(DefaultConfig.MSSOCKET_HOST), Config.getInteger(DefaultConfig.MSSOCKET_PORT))
					.sync().channel();

			this.channel = channel;

			channel.writeAndFlush(new JSONObject(test).toString());

			channel.closeFuture().syncUninterruptibly();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws ParseException {
		JSONObject obj = (JSONObject) new JSONParser().parse(msg);

		if (obj.containsKey("tag") && obj.get("tag") instanceof String && obj.containsKey("data")
				&& obj.get("data") instanceof JSONObject) {
			JSONObject data = (JSONObject) obj.get("data");
			UUID tag = UUID.fromString((String) obj.get("tag"));

			if (obj.containsKey("endpoint") && obj.get("endpoint") instanceof JSONArray && obj.containsKey("user")
					&& obj.get("user") instanceof String) {
				UUID user = UUID.fromString((String) obj.get("user"));

				Object[] endpointArr = ((JSONArray) obj.get("endpoint")).toArray();
				String[] endpoint = Arrays.copyOf(endpointArr, endpointArr.length, String[].class);

				JSONObject responseData = this.handle(endpoint, data, user);

				Map<String, Object> response = new HashMap<String, Object>();

				response.put("tag", tag.toString());
				response.put("data", responseData);

				this.send(ctx.channel(), new JSONObject(response));
			} else if (obj.containsKey("ms") && obj.get("ms") instanceof String) {
				String ms = (String) obj.get("ms");

				this.sendToMicroService(ms, this.handleFromMicroService(data), tag);
			}
		}
	}

	public abstract JSONObject handle(String[] endpoint, JSONObject data, UUID user);

	public abstract JSONObject handleFromMicroService(JSONObject data);

	private void send(Channel channel, JSONObject obj) {
		channel.writeAndFlush(Unpooled.copiedBuffer(obj.toString(), CharsetUtil.UTF_8));
	}

	public void sendToMicroService(String ms, JSONObject data, UUID tag) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();

		jsonMap.put("ms", ms);
		jsonMap.put("data", data);
		jsonMap.put("tag", tag.toString());

		this.send(channel, new JSONObject(jsonMap));
	}
	
	public void sendToUser(UUID user, JSONObject data) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		
		jsonMap.put("action", "address");
		jsonMap.put("user", user.toString());
		jsonMap.put("data", data);
		
		this.send(this.channel, new JSONObject(jsonMap));
	}

}
