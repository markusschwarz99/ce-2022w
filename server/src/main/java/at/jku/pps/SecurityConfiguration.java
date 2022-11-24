package at.jku.pps;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        final UserDetails worker = User.builder()
                .username("worker1")
                .password("{bcrypt}$2a$16$0URZSLjhp465M4TuBUlq0eQSzBsaYTgMQxxFVhoB3ViGq6sfyPqea")
                .roles("WORKER")
                .build();
        final UserDetails productionManager = User.builder()
                .username("productionManager1")
                .password("{bcrypt}$2a$16$Cy5UVmJjUmVePCyzcAdPWet8VN.clcf9UuYiYnOC6WAIQXZqUA0VS")
                .roles("PRODUCTIONMANAGER")
                .build();

        return new InMemoryUserDetailsManager(worker, productionManager);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .mvcMatchers(HttpMethod.GET, "/productionOrders").authenticated()
                        .mvcMatchers(HttpMethod.GET, "/productionOrdersSorted").authenticated()
                        .mvcMatchers(HttpMethod.POST, "/newProductionOrder").hasRole("PRODUCTIONMANAGER")
                        .mvcMatchers(HttpMethod.PUT, "/changeMachine").authenticated()
                        .mvcMatchers(HttpMethod.PUT, "/changePriority").authenticated()
                        .mvcMatchers(HttpMethod.DELETE, "/deleteProductionOrder").hasRole("PRODUCTIONMANAGER")
                        .mvcMatchers(HttpMethod.DELETE, "/deleteAllProductionOrders").hasRole("PRODUCTIONMANAGER")
                )
                .httpBasic(withDefaults());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
        return http.build();
    }
}