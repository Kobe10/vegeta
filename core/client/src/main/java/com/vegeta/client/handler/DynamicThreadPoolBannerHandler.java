package com.vegeta.client.handler;

import com.vegeta.client.config.bootstrap.BootstrapProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * banner 图
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Slf4j
@RequiredArgsConstructor
public class DynamicThreadPoolBannerHandler implements InitializingBean {

    @NonNull
    private final BootstrapProperties properties;

    private final String DYNAMIC_THREAD_POOL = " :: Dynamic ThreadPool :: ";

    private final String VEGETA_GITHUB = "";

    private final String VEGETA_SITE = "";

    private final int STRAP_LINE_SIZE = 50;

    @Override
    public void afterPropertiesSet() {
        printBanner();
    }

    private void printBanner() {
        String banner = " __      ________ _____ ______ _______\n" +
                " \\ \\    / /  ____/ ____|  ____|__   __|/\\\n" +
                "  \\ \\  / /| |__ | |  __| |__     | |  /  \\\n" +
                "   \\ \\/ / |  __|| | |_ |  __|    | | / /\\ \\\n" +
                "    \\  /  | |___| |__| | |____   | |/ ____ \\\n" +
                "     \\/   |______\\_____|______|  |_/_/    \\_\\";

        if (properties.isBanner()) {
            String version = getVersion();
            version = (version != null) ? " (v" + version + ")" : "no version.";

            StringBuilder padding = new StringBuilder();
            while (padding.length() < STRAP_LINE_SIZE - (version.length() + DYNAMIC_THREAD_POOL.length())) {
                padding.append(" ");
            }

            System.out.println(AnsiOutput.toString(banner, AnsiColor.GREEN, DYNAMIC_THREAD_POOL, AnsiColor.DEFAULT,
                    padding.toString(), AnsiStyle.FAINT, version, "\n\n", VEGETA_GITHUB, "\n", VEGETA_SITE, "\n"));
        }
    }

    public static String getVersion() {
        final Package pkg = DynamicThreadPoolBannerHandler.class.getPackage();
        return pkg != null ? pkg.getImplementationVersion() : "";
    }
}
