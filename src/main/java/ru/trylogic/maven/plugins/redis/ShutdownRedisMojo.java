package ru.trylogic.maven.plugins.redis;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo( name = "shutdown", defaultPhase = LifecyclePhase.NONE )
public class ShutdownRedisMojo  extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        if(RedisServerRegistry.redisGroup == null) {
            getLog().error("Redis server is not running");
            return;
        }

        getLog().info("Shutting down Redis server...");
        RedisServerRegistry.redisGroup.shutdownGracefully();
        
        while(!RedisServerRegistry.redisGroup.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                getLog().info(e);
            }
        }
    }
}