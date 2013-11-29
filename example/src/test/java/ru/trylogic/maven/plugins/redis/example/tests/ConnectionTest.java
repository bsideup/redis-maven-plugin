package ru.trylogic.maven.plugins.redis.example.tests;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class ConnectionTest {
    
    @Test
    public void testConnection() {
        Jedis jedis = new Jedis("localhost");
        
        System.out.println(jedis.ping());
        
        jedis.set("test", "123");
        
        Assert.assertEquals("123", jedis.get("test"));
        
        try {
            jedis.quit();
        } catch (Exception e) {
        }
    }
    
}
