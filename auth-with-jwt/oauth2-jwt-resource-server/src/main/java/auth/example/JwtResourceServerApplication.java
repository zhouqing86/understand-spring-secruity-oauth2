package auth.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtResourceServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtResourceServerApplication.class);
    }

    @RestController
    @RequestMapping("/")
    protected static class HelloController {

//        @PreAuthorize("hasAuthority('FOO_WRITE')")
        @GetMapping
        public String home() {
            return "home";
        }
    }

     @Configuration
     protected static class JwtConfiguration {
        @Autowired
        JwtAccessTokenConverter jwtAccessTokenConverter;


        @Bean
        @Qualifier("tokenStore")
        public TokenStore tokenStore() {

            System.out.println("Created JwtTokenStore");
            return new JwtTokenStore(jwtAccessTokenConverter);
        }

        @Bean
        protected JwtAccessTokenConverter jwtTokenEnhancer() {
            JwtAccessTokenConverter converter =  new JwtAccessTokenConverter();
            Resource resource = new ClassPathResource("public.cert");
            String publicKey = null;
            try {
                publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            converter.setVerifierKey(publicKey);
            return converter;
        }
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenStore(tokenStore);
        }

        @Autowired
        TokenStore tokenStore;
    }

}
