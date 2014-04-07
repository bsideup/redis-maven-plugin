package ru.trylogic.maven.plugins.redis.tests;

import org.apache.maven.plugin.MojoExecutionException;
import redis.clients.jedis.Jedis;
import ru.trylogic.maven.plugins.redis.RunRedisMojo;
import ru.trylogic.maven.plugins.redis.ShutdownRedisMojo;

public class RunRedisMojoTest extends AbstractRedisMojoTest {

    public static final String SIMPLE_POM_FILE = "src/test/resources/unit/pom.xml";

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

        final Jedis jedis = new Jedis("localhost", runRedisMojo.port);
        waitUntilConnect(jedis);

        assertEquals("OK", jedis.set(TEST_KEY, TEST_VALUE));
        assertEquals(TEST_VALUE, jedis.get(TEST_KEY));

        try {
            jedis.quit();
        } catch(Exception ignored) {

        }

        final ShutdownRedisMojo shutdownRedisMojo = lookupRedisMojo(SIMPLE_POM_FILE, "shutdown");
        shutdownRedisMojo.execute();

        testConnectionDown(jedis);
    }
}
