package run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import run.model.UserDetail;
import run.repo.FriendRepository;
import run.repo.UserDetailRepository;
import run.services.MapMyRunService;

@SpringBootApplication
@RestController
@EnableOAuth2Client
@EnableOAuth2Sso
@EnableAuthorizationServer
@Order(6)
public class SocialApplication extends WebSecurityConfigurerAdapter {
    private static Log logger = LogFactory.getLog(SocialApplication.class);
	@Autowired OAuth2ClientContext oauth2ClientContext;
	@Autowired MapMyRunService runService;
	@Autowired UserDetailRepository userRepo;
	@Autowired FriendRepository friendRepo;
    @Value("${mapmyrun.client.pre-established-redirect-uri}")
    private String user_auth_uri;

	@Autowired
	private RTEAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user1").password("user1Pass").authorities("ROLE_USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.antMatcher("/**").authorizeRequests().antMatchers("/", "/user**", "/auth**", "/img/**", "/css**", "/js**", "/callback**", "/logout**", "/login**", "/webjars/**").permitAll().anyRequest()
				.authenticated().and().exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")).and().logout()
				.logoutSuccessUrl("/").permitAll().and().csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
		// @formatter:on
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.antMatcher("/me").authorizeRequests().anyRequest().authenticated();
			// @formatter:on
		}
	}

	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	@Bean(name="mapmyrun")
	@ConfigurationProperties("mapmyrun")
	public ClientResources mapmyrun() {
		ClientResources cr = new ClientResources();
		cr.getClient().setUseCurrentUri(false);
		cr.getClient().setPreEstablishedRedirectUri(user_auth_uri);
		return cr;
	}
	@Bean
	public OAuth2ClientContext oauth2ClientContext() {
		return oauth2ClientContext;
	}
	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		ClientResources client = mapmyrun();
		filters.add(ssoFilter(client, "/login/mapmyrun"));
		filter.setFilters(filters);
		return filter;
	}
	private Filter ssoFilter(ClientResources client, String path) {
		try {
			OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
			OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext());
			filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
				
				@Override
				public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
						AuthenticationException exception) throws IOException, ServletException {
					logger.info("AUTH FAILED");
					exception.printStackTrace();
					response.sendRedirect("/");
					
				}
			});
			filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
				
				@Override
				public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
					try {
						request.getSession().setAttribute("token", authentication.getPrincipal());
						request.getSession().setAttribute("isNew", true);
						UserDetail user = runService.getUserDetails();
//						if (userRepo.countByUserId(user.getUserId()) == 0) {
							logger.info("Saving useR: " + user);
							userRepo.save(user);
//						}
						request.getSession().setAttribute("user", user);
						runService.updateFriends();
						response.sendRedirect("/auth/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			filter.setRestTemplate(template);
			filter.setTokenServices(new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId()));
			return filter;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Bean("restOperations")
	public RestOperations mapMyRunController() throws Exception {
		OAuth2RestTemplate template = new OAuth2RestTemplate(mapmyrun().getClient(), oauth2ClientContext());
		return template;
	}
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        SimpleModule module = new SimpleModule();
        mapper.registerModule(module);
      
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        return converter;
    }
    @RequestMapping({"/auth/"})
	public ModelAndView home() {
		return new ModelAndView("/auth/index.html");
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialApplication.class, args);
	}


}

class ClientResources {
    private static Log logger = LogFactory.getLog(ClientResources.class);

	@NestedConfigurationProperty
	private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

	@NestedConfigurationProperty
	private ResourceServerProperties resource = new ResourceServerProperties();

	public AuthorizationCodeResourceDetails getClient() {
//		logger.info(client.getClientId() + " " + client.getAccessTokenUri());
		return client;
	}

	public ResourceServerProperties getResource() {
		return resource;
	}
}
