package net.cryptic_game.microservice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

			channel.writeAndFlush(new JSONObject(test).toString());

			channel.closeFuture().syncUninterruptibly();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		JSONObject obj = (JSONObject) new JSONParser().parse(msg);

		if (obj.containsKey("tag") && obj.get("tag") instanceof String && obj.containsKey("data")
				&& obj.get("data") instanceof JSONObject && obj.containsKey("endpoint")
				&& obj.get("endpoint") instanceof JSONArray) {
			Object[] endpointArr = ((JSONArray) obj.get("endpoint")).toArray();
			String[] endpoint = Arrays.copyOf(endpointArr, endpointArr.length, String[].class);
			
			JSONObject responseData = this.handle(endpoint, (JSONObject) obj.get("data"));

			Map<String, Object> response = new HashMap<String, Object>();

			response.put("tag", (String) obj.get("tag"));
			response.put("data", responseData);

			this.send(ctx.channel(), new JSONObject(response));
		}
	}

	public abstract JSONObject handle(String[] endpoint, JSONObject data);

	private void send(Channel channel, JSONObject obj) {
		channel.writeAndFlush(Unpooled.copiedBuffer(obj.toString(), CharsetUtil.UTF_8));
	}

}
