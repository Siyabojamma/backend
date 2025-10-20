package com.kaiburr.taskbackend.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class CommandValidator {

    // ✅ Whitelist of allowed commands
    private static final Set<String> WHITELIST = new HashSet<>(Arrays.asList(
            "echo", "ls", "date", "uname", "cat", "sleep", "printf"
    ));

    // ✅ Properly escaped forbidden pattern (double backslashes for Java)
    private static final Pattern FORBIDDEN = Pattern.compile(
            ".*(;|&&|\\|\\||\\||>|<|\\$\\(|`|sudo|rm|curl|wget|nc|ssh|python -c).*",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean isSafe(String command) {
        if (command == null || command.trim().isEmpty()) return false;
        if (FORBIDDEN.matcher(command).matches()) return false;

        String first = command.trim().split("\\s+")[0];
        return WHITELIST.contains(first);
    }
}
