package com.adobe.locust.impl;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import picocli.CommandLine;

import java.util.List;

@OpenAPIDefinition(
    info = @Info(
            title = Application.TITLE,
            description = Application.DESCRIPTION,
            version = "0.0"
    )
)
@CommandLine.Command(name = Application.TITLE, description = Application.DESCRIPTION,
        mixinStandardHelpOptions = true)
public class Application implements Runnable {
    public static final String TITLE = "locust-cluster-proxy-cli";
    public static final String DESCRIPTION = "This server act as a proxy to aggregate data from multple locust manager sources";
    @CommandLine.Option(names = {"-t", "--targets"}, defaultValue = "${env:TARGET_LOCUSTS}", description = "List of targets", split = ",", required = true)
    public static List<String> targets;
    public static void main(String[] args) {
        PicocliRunner.run(Application.class, args);
    }
    @Override
    public void run() {
        Micronaut.build(new String[]{})
                .eagerInitSingletons(true)
                .mainClass(Application.class)
                .start();
    }
}