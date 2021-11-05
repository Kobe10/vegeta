package com.vegeta.server;

/**
 * <p></p>
 * <p> xx
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年11月05日 14:37
 * @since 1.0
 */
@MapperScan(basePackages =
        {
                "com.github.dynamic.threadpool.config.mapper",
                "com.github.dynamic.threadpool.auth.mapper"
        })
@SpringBootApplication(scanBasePackages =
        {
                "com.github.dynamic.threadpool"
        })
public class ServerApplication {
}
