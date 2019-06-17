package net.cryptic_game.microservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

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
import net.cryptic_game.microservice.endpoint.MicroserviceEndpoint;
import net.cryptic_game.microservice.endpoint.UserEndpoint;
import net.cryptic_game.microservice.utils.Tuple;

public abstract class MicroService extends SimpleChannelInboundHandler<String> {

	private static MicroService instance = null;

	public static MicroService getInstance() {
		return instance;
	}

	private static final boolean EPOLL = Epoll.isAvailable();

	private Map<UUID, JSONObject> inter = new HashMap<UUID, JSONObject>();

	private Map<List<String>, Tuple<UserEndpoint, Method>> userEndpoints = new HashMap<List<String>, Tuple<UserEndpoint, Method>>();
	private Map<List<String>, Tuple<MicroserviceEndpoint, Method>> microserviceEndpoints = new HashMap<List<String>, Tuple<MicroserviceEndpoint, Method>>();

	private String name;
	private Channel channel;

	public MicroService(String name) {		
		this.name = name;

		instance = this;

		this.init();
		this.start();
	}

	public String getName() {
		return name;
	}

	private void start() {
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

	private void init() {
		Reflections reflections = new Reflections("net.cryptic_game.microservice", new MethodAnnotationsScanner());
		{
			Set<Method> methods = reflections.getMethodsAnnotatedWith(UserEndpoint.class);
			for (Method method : methods) {
				UserEndpoint methodEndpoint = method.getAnnotation(UserEndpoint.class);

				userEndpoints.put(Arrays.asList(methodEndpoint.path()),
						new Tuple<UserEndpoint, Method>(methodEndpoint, method));
			}
		}
		{
			Set<Method> methods = reflections.getMethodsAnnotatedWith(MicroserviceEndpoint.class);
			for (Method method : methods) {
				MicroserviceEndpoint methodEndpoint = method.getAnnotation(MicroserviceEndpoint.class);

				microserviceEndpoints.put(Arrays.asList(methodEndpoint.path()),
						new Tuple<MicroserviceEndpoint, Method>(methodEndpoint, method));
			}
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) {
		final MicroService m = this;

		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject obj = new JSONObject();
				try {
					obj = (JSONObject) new JSONParser().parse(msg);
				} catch (ParseException e) {
				}

				if (obj.containsKey("tag") && obj.get("tag") instanceof String && obj.containsKey("data")
						&& obj.get("data") instanceof JSONObject) {
					JSONObject data = (JSONObject) obj.get("data");

					UUID tag = UUID.fromString((String) obj.get("tag"));

					if (obj.containsKey("endpoint") && obj.get("endpoint") instanceof JSONArray) {
						Object[] endpointArr = ((JSONArray) obj.get("endpoint")).toArray();
						String[] endpoint = Arrays.copyOf(endpointArr, endpointArr.length, String[].class);

						if (obj.containsKey("user") && obj.get("user") instanceof String) {
							UUID user = UUID.fromString((String) obj.get("user"));

							JSONObject responseData = handle(endpoint, data, user);

							Map<String, Object> response = new HashMap<String, Object>();

							response.put("tag", tag.toString());
							response.put("data", responseData);

							m.send(ctx.channel(), new JSONObject(response));
						} else if (obj.containsKey("ms") && obj.get("ms") instanceof String) {
							String ms = (String) obj.get("ms");

							if (!m.inter.containsKey(tag)) {
								m.sendToMicroService(ms, handleFromMicroService(endpoint, data, ms), tag);
							} else {
								m.inter.replace(tag, data);
							}
						}
					}
				}
			}
		}).start();
	}

	public JSONObject handle(String[] endpointArray, JSONObject data, UUID user) {
		List<String> endpoint = Arrays.asList(endpointArray);

		if (userEndpoints.containsKey(endpoint)) {
			Tuple<UserEndpoint, Method> tuple = userEndpoints.get(endpoint);

			UserEndpoint e = tuple.getA();
			Method eMethod = tuple.getB();
			if (checkData(e.keys(), e.types(), data)) {
				try {
					JSONObject result = (JSONObject) eMethod.invoke(new Object(), data, user);

					if (result == null) {
						result = new JSONObject(new HashMap<String, String>());
					}

					return result;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					Map<String, String> jsonMap = new HashMap<String, String>();

					jsonMap.put("error", "internal error");

					return new JSONObject(jsonMap);
				}
			} else {
				Map<String, String> jsonMap = new HashMap<String, String>();

				jsonMap.put("error", "invalid input data");

				return new JSONObject(jsonMap);
			}
		}

		Map<String, String> jsonMap = new HashMap<String, String>();

		jsonMap.put("error", "unknown service");

		return new JSONObject(jsonMap);
	}

	public JSONObject handleFromMicroService(String[] endpointArray, JSONObject data, String ms) {
		List<String> endpoint = Arrays.asList(endpointArray);

		if (microserviceEndpoints.containsKey(endpoint)) {
			Tuple<MicroserviceEndpoint, Method> tuple = microserviceEndpoints.get(endpoint);

			MicroserviceEndpoint e = tuple.getA();
			Method eMethod = tuple.getB();
			if (checkData(e.keys(), e.types(), data)) {
				try {
					JSONObject result = (JSONObject) eMethod.invoke(new Object(), data, ms);

					if (result == null) {
						result = new JSONObject(new HashMap<String, String>());
					}

					return result;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					Map<String, String> jsonMap = new HashMap<String, String>();

					jsonMap.put("error", "internal error");

					return new JSONObject(jsonMap);
				}
			} else {
				Map<String, String> jsonMap = new HashMap<String, String>();

				jsonMap.put("error", "invalid input data");

				return new JSONObject(jsonMap);
			}
		}

		Map<String, String> jsonMap = new HashMap<String, String>();

		jsonMap.put("error", "unknown service");

		return new JSONObject(jsonMap);
	}

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

	private static boolean checkData(String[] keys, Class<?>[] types, JSONObject obj) {
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Class<?> type = types[i];

			if (!obj.containsKey(key) || obj.get(key).getClass() != type) {
				return false;
			}
		}

		return true;
	}

	public JSONObject contactMicroservice(String ms, String[] endpoint, JSONObject data) {
		UUID tag = UUID.randomUUID();

		Map<String, Object> jsonMap = new HashMap<String, Object>();

		jsonMap.put("ms", ms);
		jsonMap.put("data", data);
		jsonMap.put("endpoint", Arrays.asList(endpoint));
		jsonMap.put("tag", tag.toString());

		this.send(channel, new JSONObject(jsonMap));

		this.inter.put(tag, null);

		int counter = 0;

		while (this.inter.get(tag) == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			counter++;

			if (counter > 100 * 30) {
				return null;
			}
		}

		return this.inter.remove(tag);
	}

}
