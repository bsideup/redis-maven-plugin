package ru.trylogic.maven.plugins.redis.tests;

import org.apache.maven.plugin.MojoExecutionException;
import redis.clients.jedis.Jedis;
import ru.trylogic.maven.plugins.redis.RunRedisMojo;
import ru.trylogic.maven.plugins.redis.ShutdownRedisMojo;

public class RunForkedRedisMojoTest extends AbstractRedisMojoTest {

    public static final String FORKED_POM_FILE = "src/test/resources/unit/pom.xml";

    public void testRunForked() throws Exception {
        final RunRedisMojo runRedisMojo = lookupRedisMojo(FORKED_POM_FILE, "run");
        assertNotNull(runRedisMojo);

        runRedisMojo.forked = true;
        
        runRedisMojo.execute();

        final Jedis jedis = new Jedis("localhost", runRedisMojo.port);
        waitUntilConnect(jedis);

        assertEquals("OK", jedis.set(TEST_KEY, TEST_VALUE));
        assertEquals(TEST_VALUE, jedis.get(TEST_KEY));

        try {
            jedis.quit();
        } catch(Exception ignored) {
            
        }

        final ShutdownRedisMojo shutdownRedisMojo = lookupRedisMojo(FORKED_POM_FILE, "shutdown");
        shutdownRedisMojo.execute();

        testConnectionDown(jedis);
    }
}
