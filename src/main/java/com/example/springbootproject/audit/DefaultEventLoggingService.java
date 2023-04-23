package com.example.springbootproject.audit;


//Reference: https://github.com/gchq/event-logging

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultEventLoggingService implements EventLoggingService {

    private static final String SYSTEM_USER = "SYS_USER";
    private static final String IPADDR_ANY = "0.0.0.0";
    private static final String SYSTEM = "Spring-boot Application";
    private static final String ENVIRONMENT = "DEV";
    private static final String VERSION = "1.2.3";

    private Device device;

    @Autowired
    private EventLoggingRepository eventLoggingRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostConstruct
    private void init() {
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            final String hostname = localHost.getHostName();
            final String hostAddress = localHost.getHostAddress();

            String macAddress = "";  // empty string by default
            try {
                // Just get the MAC of the first network interface for illustration purposes
                final NetworkInterface ni = NetworkInterface.getNetworkInterfaces().nextElement();
                byte[] hardwareAddress = ni.getHardwareAddress();
                if (hardwareAddress != null) {
                    final String[] hexadecimal = new String[hardwareAddress.length];
                    for (int i = 0; i < hardwareAddress.length; i++) {
                        hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
                    }
                    macAddress = String.join("-", hexadecimal);
                }

            } catch (SocketException e) {
                log.warn("Unable to get MAC address due to", e);
            }

            device = Device.builder()
                    .hostname(hostname)
                    .hostAddress(hostAddress)
                    .macAddress(macAddress)
                    .osUser(getOsUser())
                    .build();

        } catch (UnknownHostException e) {
            log.error("Unable to determine device address", e);
            device = Device.DEVICE_NULL;
        }

    }

    private String getOsUser() {
        return System.getProperty("user.name");
    }

    @Override
    public void logEvent(final @NonNull EventAction eventAction) {

//        USAGE:
//        EventAction succeededLoginEventAction = LoginSuccessEventAction.builder()
//                .action(Action.SUCCEEDED_LOGIN)
//                .message("")
//                .build();

        Event event = Event.builder()
                .eventTime(EventTime.builder()
                        .timeCreated(new Date())
                        .build())
                .eventSource(EventSource.builder()
                        .systemDetail(SystemDetail.builder()
                                .system(SYSTEM)
                                .environment(ENVIRONMENT)
                                .version(VERSION)
                                .build())
                        .device(device)
                        .client(Client.builder()
                                .ip(getRequestIp())
                                .browser(getRequestAgent())
                                .referer(getRequestReferer())
                                .build())
                        .user(UserRequestInfo.builder()
                                .username(getLoggedInUser())
                                .userRole(getLoggedInUserRole())
                                .build())
                        .build())
                .eventDetail(EventDetail.builder()
                        .eventAction(eventAction)
                        .build())
                .build();

        eventLoggingRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents() {

        return eventLoggingRepository.getAllRecords();
    }

    private String getLoggedInUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (!Objects.isNull(auth) && auth.isAuthenticated()) {
        if (!Objects.isNull(auth) && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }

        return SYSTEM_USER;
    }

    private String getLoggedInUserRole() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(auth) && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return ((UserDetails) auth.getPrincipal()).getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.joining());
        }

        return "";
    }

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private String getRequestIp() {

        if (RequestContextHolder.getRequestAttributes() == null) {
            return IPADDR_ANY;
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0];
                return ip;
            }
        }

        String ipAddress = request.getRemoteAddr();

        return ipAddress != null ? ipAddress : IPADDR_ANY;
    }


    private String getRequestAgent() {
        String agentName = httpServletRequest.getHeader(HttpHeaders.USER_AGENT);

        return agentName == null ? "" : agentName;
    }

    private String getRequestReferer() {
        String referer = httpServletRequest.getHeader(HttpHeaders.REFERER);

        return referer == null ? "" : referer;
    }

}
