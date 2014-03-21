package ru.trylogic.maven.plugins.redis;


import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = "shutdown", defaultPhase = LifecyclePhase.NONE )
public class ShutdownRedisMojo  extends AbstractMojo {

    @Parameter(property = "redis.server.skip", defaultValue = "false")
    public boolean skip;

    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Redis server had been skipped...");
            return;
        }

        DefaultEventExecutorGroup redisGroup = (DefaultEventExecutorGroup) getPluginContext().get(RunRedisMojo.REDIS_GROUP_CONTEXT_PROPERTY_NAME);

        if(redisGroup == null) {
            throw new MojoExecutionException("Redis server is not running");
        }

        getLog().info("Shutting down Redis server...");
        redisGroup.shutdownGracefully();
    }
}