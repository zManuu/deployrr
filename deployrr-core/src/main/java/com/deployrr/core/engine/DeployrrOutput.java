package com.deployrr.core.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class DeployrrOutput {

    private static final Logger LOG = LogManager.getLogger(DeployrrOutput.class);
    private static final int LINE_LENGTH = 72;
    private static final String MINUS = "-";
    private static final String BANNER = "" +
            "██████╗░███████╗██████╗░██╗░░░░░░█████╗░██╗░░░██╗██████╗░██████╗░\n" +
            "██╔══██╗██╔════╝██╔══██╗██║░░░░░██╔══██╗╚██╗░██╔╝██╔══██╗██╔══██╗\n" +
            "██║░░██║█████╗░░██████╔╝██║░░░░░██║░░██║░╚████╔╝░██████╔╝██████╔╝\n" +
            "██║░░██║██╔══╝░░██╔═══╝░██║░░░░░██║░░██║░░╚██╔╝░░██╔══██╗██╔══██╗\n" +
            "██████╔╝███████╗██║░░░░░███████╗╚█████╔╝░░░██║░░░██║░░██║██║░░██║\n" +
            "╚═════╝░╚══════╝╚═╝░░░░░╚══════╝░╚════╝░░░░╚═╝░░░╚═╝░░╚═╝╚═╝░░╚═╝\n ";

    private DeployrrOutput() {}

    public static void line() {
        LOG.info(MINUS.repeat(LINE_LENGTH));
    }

    public static void line(String line) {
        line = String.format("[ %s ]", line);
        int lineLength = (LINE_LENGTH - line.length()) / 2;
        line = MINUS.repeat(lineLength) + line;
        LOG.info("{}{}", line, MINUS.repeat(LINE_LENGTH - line.length()));
    }

    public static void banner() {
        Arrays.stream(BANNER.split("\n"))
                .forEach(LOG::info);
    }

}
