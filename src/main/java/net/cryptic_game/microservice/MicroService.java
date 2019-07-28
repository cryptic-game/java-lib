package net.cryptic_game.microservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.cryptic_game.microservice.utils.JSONUtils;
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

    private static final boolean EPOLL = Epoll.isAvailable();

    private static MicroService instance;

    private Map<UUID, JSONObject> inter = new HashMap<UUID, JSONObject>();

    private Map<List<String>, Tuple<UserEndpoint, Method>> userEndpoints = new HashMap<>();
    private Map<List<String>, Tuple<MicroserviceEndpoint, Method>> microserviceEndpoints = new HashMap<>();

    private String name;
    private Channel channel;

    public static MicroService getInstance() {
        return instance;
    }

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
            JSONObject registration = JSONUtils.json().add("action", "register").add("name", this.getName()).build();

            Channel channel = new Bootstrap().group(group)
                    .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .handler(new MicroServiceInitializer(this))
                    .connect(Config.get(DefaultConfig.MSSOCKET_HOST), Config.getInteger(DefaultConfig.MSSOCKET_PORT))
                    .sync().channel();

            this.channel = channel;

            channel.writeAndFlush(registration.toString());

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
                        new Tuple<>(methodEndpoint, method));
            }
        }
        {
            Set<Method> methods = reflections.getMethodsAnnotatedWith(MicroserviceEndpoint.class);
            for (Method method : methods) {
                MicroserviceEndpoint methodEndpoint = method.getAnnotation(MicroserviceEndpoint.class);

                microserviceEndpoints.put(Arrays.asList(methodEndpoint.path()),
                        new Tuple<>(methodEndpoint, method));
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        new Thread(() -> {

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

                       this.send(ctx.channel(), JSONUtils.json().add("tag", tag.toString()).add("data", responseData).build());
                    } else if (obj.containsKey("ms") && obj.get("ms") instanceof String) {
                        String ms = (String) obj.get("ms");

                        if (!this.inter.containsKey(tag)) {
                            this.sendToMicroService(ms, handleFromMicroService(endpoint, data, ms), tag);
                        } else {
                            this.inter.replace(tag, data);
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
                        result = JSONUtils.empty();
                    }

                    return result;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    return JSONUtils.error("internal error");
                }
            } else {
                return JSONUtils.error("invalid input  data");
            }
        }

        return JSONUtils.error("unknown service");
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
                        result = JSONUtils.empty();
                    }

                    return result;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    return JSONUtils.error("internal error");
                }
            } else {
                return JSONUtils.error("invalid input  data");
            }
        }
        return JSONUtils.error("unknown service");
    }


    private void send(Channel channel, JSONObject obj) {
        channel.writeAndFlush(Unpooled.copiedBuffer(obj.toString(), CharsetUtil.UTF_8));
    }

    public void sendToMicroService(String ms, JSONObject data, UUID tag) {
        this.send(channel, JSONUtils.json().add("ms", ms).add("data", data).add("tag", tag.toString()).build());
    }

    public void sendToUser(UUID user, JSONObject data) {
        this.send(channel, JSONUtils.json().add("action", "address").add("user", user.toString()).add("data", data).build());
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

        this.send(channel, JSONUtils.json().add("ms", ms).add("data", data).add("endpoint", Arrays.asList(endpoint)).add("tag", tag.toString()).build());

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
