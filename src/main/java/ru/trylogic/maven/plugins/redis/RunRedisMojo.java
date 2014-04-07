package ru.trylogic.maven.plugins.redis;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import redis.server.netty.RedisCommandDecoder;
import redis.server.netty.RedisCommandHandler;
import redis.server.netty.RedisReplyEncoder;
import redis.server.netty.SimpleRedisServer;

@Mojo(name = "run", defaultPhase = LifecyclePhase.NONE)
public class RunRedisMojo extends AbstractMojo {

    public static final String REDIS_GROUP_CONTEXT_PROPERTY_NAME = RunRedisMojo.class.getName() + ":redisGroup";

    @Parameter(property = "redis.server.port", defaultValue = "6379")
    public Integer port;

    @Parameter(property = "redis.server.forked", defaultValue = "false")
    public boolean forked;

    @Parameter(property = "redis.server.skip", defaultValue = "false")
    public boolean skip;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping Redis server...");
            return;
        }
        doExecute(forked);
    }

    public void doExecute(boolean forked) {
        final RedisCommandHandler commandHandler = new RedisCommandHandler(new SimpleRedisServer());

        final DefaultEventExecutorGroup redisGroup = new DefaultEventExecutorGroup(1);

        getPluginContext().put(REDIS_GROUP_CONTEXT_PROPERTY_NAME, redisGroup);

        ServerBootstrap redisServerBootstrap = new ServerBootstrap();

        try {
            redisServerBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .localAddress(port)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new RedisCommandDecoder());
                            p.addLast(new RedisReplyEncoder());
                            p.addLast(redisGroup, commandHandler);
                        }
                    });

            getLog().info("Starting Redis(forked=" + forked + ", port=" + port + ") server...");
            ChannelFuture future = redisServerBootstrap.bind();

            if (!forked) {
                getLog().info("Press Ctrl-C to stop Redis...");
                try {
                    ChannelFuture syncFuture = future.sync();
                    syncFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    getLog().info(e);
                    redisGroup.shutdownGracefully();
                }
            }
        } finally {
            if (!forked) {
                redisGroup.shutdownGracefully();
            }
        }
    }
}
