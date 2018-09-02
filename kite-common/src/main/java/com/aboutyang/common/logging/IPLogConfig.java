package com.aboutyang.common.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.aboutyang.common.utils.IPUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author aboutyang
 */
public class IPLogConfig extends ClassicConverter {

    public String hostIpCache;

    @Override
    public String convert(ILoggingEvent event) {
        if(StringUtils.isBlank(hostIpCache)){
            hostIpCache = IPUtils.getHostIp();
        }
        return hostIpCache;
    }

}
