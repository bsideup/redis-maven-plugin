redis-maven-plugin [![Build Status](https://travis-ci.org/bsideup/redis-maven-plugin.png)](https://travis-ci.org/bsideup/redis-maven-plugin)
==================

Embedded pure Java redis server for Maven 3. Based on great https://github.com/spullara/redis-protocol.


Basic example
-----------------

add plugin to your pom:
```xml
<plugin>
    <groupId>ru.trylogic.maven.plugins</groupId>
    <artifactId>redis-maven-plugin</artifactId>
    <version>1.2.3</version>
</plugin>
```

run ```mvn redis:run```

Is you see this message you are ready to go: ```[INFO] Starting Redis(forked=false) server...```


Integration tests example
-----------------

(This example also available here: https://github.com/bsideup/redis-maven-plugin/tree/master/example )

Configure plugin as follow:
```xml
<plugin>
    <groupId>ru.trylogic.maven.plugins</groupId>
    <artifactId>redis-maven-plugin</artifactId>
    <version>1.2.3</version>
    <configuration>
        <forked>true</forked>
    </configuration>
    <executions>
        <execution>
            <id>start-redis</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>run</goal>
            </goals>
        </execution>
        <execution>
            <id>stop-redis</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>shutdown</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Now you will be able to run your integration Redis-backed tests with ```mvn clean verify```

License
-----------------

Copyright (c) 2013 Sergei bsideup Egorov

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
