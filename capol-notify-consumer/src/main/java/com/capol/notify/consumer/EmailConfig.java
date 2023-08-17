package com.capol.notify.consumer;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailConfig {

    private final Smtp smtp= new Smtp();

    private final Transport transport = new Transport();

    private String username;

    private String password;

    private String sender;

    public static class Smtp{

        private String host;

        private String auth;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }
    }


    public static class Transport{

        private String protocol;

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
    }
}