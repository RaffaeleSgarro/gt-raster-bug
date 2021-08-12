# Bug description

The same program that uses `PolygonExtractionProcess` works perfectly fine when
run from Intellij or from `gradle bootRun` but throws an Exception when run from
the executable JAR generated by `gradle bootJar`.

Obviously this is a loading problem because maybe some files in `META-INF` and/or
whatnot required by JAI are not in a place where the classloader in charge can find
them.

This is the stack trace of the error:

```
java.lang.IllegalArgumentException: The input argument(s) may not be null.
        at javax.media.jai.ParameterBlockJAI.getDefaultMode(ParameterBlockJAI.java:136)
        at javax.media.jai.ParameterBlockJAI.<init>(ParameterBlockJAI.java:157)
        at javax.media.jai.ParameterBlockJAI.<init>(ParameterBlockJAI.java:178)
        at org.geotools.process.raster.PolygonExtractionProcess.execute(PolygonExtractionProcess.java:189)
        at com.example.demo.DemoApplication$TriggerBug.run(DemoApplication.java:67)
        at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:782)
```


## Steps to reproduce

```
./gradlew bootJar
java -jar build/libs/gt-raster-bug.jar
```

## Steps to NOT reproduce

```
./gradlew bootRun
```
