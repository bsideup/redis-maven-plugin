package ru.trylogic.maven.plugins.redis.tests;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract public class AbstractRedisMojoTest extends AbstractMojoTestCase {

    public static final String TEST_KEY = "testKey";
    public static final String TEST_VALUE = "testValue";
    
    protected final Map pluginContext = new ConcurrentHashMap();
    
    protected <T extends AbstractMojo> T lookupRedisMojo(String file, String mojo) throws Exception {
        File pomFile = getTestFile(file);
        assertNotNull(pomFile);
        assertTrue(pomFile.exists());

        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(pomFile, buildingRequest).getProject();

        T result = (T) lookupConfiguredMojo(project, mojo);
        result.setPluginContext(pluginContext);
        return result;
    }

    protected void testConnectionDown(Jedis jedis) {
        try {
            jedis.ping();
            fail();
        } catch (Throwable ignored) {}
    }
    
    protected void waitUntilConnect(Jedis jedis) throws InterruptedException {
        int attempts = 200;
        while(true) {
            if(--attempts <= 0) {
                fail();
            }

            try {
                jedis.ping();
                break;
            } catch (Throwable ignored) {
                Thread.sleep(50);
            }
        }
    }
}
