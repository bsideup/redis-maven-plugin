package ru.trylogic.maven.plugins.redis.tests;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import redis.clients.jedis.Jedis;
import ru.trylogic.maven.plugins.redis.RunRedisMojo;
import ru.trylogic.maven.plugins.redis.ShutdownRedisMojo;

import java.io.File;
import java.net.ConnectException;

public class RunRedisMojoTest extends AbstractRedisMojoTest {

    public static final String SIMPLE_POM_FILE = "src/test/resources/unit/pom-simple.xml";

    public static final String FORKED_POM_FILE = "src/test/resources/unit/pom-forked.xml";

    public void testRun() throws Exception {
        final RunRedisMojo runRedisMojo = lookupRedisMojo(SIMPLE_POM_FILE, "run");
        assertNotNull(runRedisMojo);

        Thread redisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runRedisMojo.execute();
                } catch (MojoExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        redisThread.start();

        Jedis jedis = new Jedis("localhost", runRedisMojo.port);
        waitUntilConnect(jedis);

        assertEquals("OK", jedis.set(TEST_KEY, TEST_VALUE));
        assertEquals(TEST_VALUE, jedis.get(TEST_KEY));

        
        jedis.disconnect();
        final ShutdownRedisMojo shutdownRedisMojo = lookupRedisMojo(FORKED_POM_FILE, "shutdown");
        shutdownRedisMojo.execute();

        testConnectionDown(jedis);
    }

    public void testRunForked() throws Exception {
        final RunRedisMojo runRedisMojo = lookupRedisMojo(FORKED_POM_FILE, "run");
        assertNotNull(runRedisMojo);

        runRedisMojo.forked = true;
        
        runRedisMojo.execute();

        Jedis jedis = new Jedis("localhost", runRedisMojo.port);
        waitUntilConnect(jedis);

        assertEquals("OK", jedis.set(TEST_KEY, TEST_VALUE));
        assertEquals(TEST_VALUE, jedis.get(TEST_KEY));

        jedis.disconnect();
        final ShutdownRedisMojo shutdownRedisMojo = lookupRedisMojo(FORKED_POM_FILE, "shutdown");
        shutdownRedisMojo.execute();

        testConnectionDown(jedis);
    }
}
