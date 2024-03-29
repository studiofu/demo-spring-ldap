package demoldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.anyRequest().fullyAuthenticated()
				.and()
			.formLogin();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.ldapAuthentication()
				//.userDnPatterns("uid={0},ou=people")
				.userSearchFilter("(uid={0})")
				.groupSearchBase("ou=groups")
//	            .userSearchFilter("(sAMAccountName={0})")
//	            .userSearchBase("dc=XXXX,dc=XXXXXX,dc=XXX")
//	            .groupSearchBase("ou=XXXXXXX,dc=XXXX,dc=XXXXXX,dc=XXX")
//	            .groupSearchFilter("member={0}")
				.contextSource()
					.url("ldap://localhost:8389/dc=springframework,dc=org")
					.and()
				.passwordCompare()
					.passwordEncoder(new LdapShaPasswordEncoder())
					//.passwordEncoder(bcryptPasswordEncoder())
					.passwordAttribute("userPassword");
	}
	
//	// manual sha encoder
//	private PasswordEncoder shaPasswordEncoder() {
//	    final LdapShaPasswordEncoder sha = new LdapShaPasswordEncoder();
//	    return new PasswordEncoder() {
//	        @Override
//	        public String encode(CharSequence rawPassword) {
//	        	//return sha.encodePassword(rawPassword.toString(), null);
//	            return sha.encode(rawPassword);
//	        }
//	        @Override
//	        public boolean matches(CharSequence rawPassword, String encodedPassword) {
//	        	return sha.matches(rawPassword, encodedPassword);
//	            //return sha.isPasswordValid(encodedPassword, rawPassword.toString(), null);
//	        }
//	    };
//	}
//	
	// manual bcrypt encoder
	private PasswordEncoder bcryptPasswordEncoder() {
		  final BCryptPasswordEncoder crypt = new BCryptPasswordEncoder();
		  return new PasswordEncoder() {
		    
			  @Override
		    public String encode(CharSequence rawPassword) {
		      // Prefix so that apache directory understands that bcrypt has been used.
		      // Without this, it assumes SSHA and fails during authentication.
		      return "{CRYPT}" + crypt.encode(rawPassword);
		    }
		    
		    @Override
		    public boolean matches(CharSequence rawPassword, String encodedPassword) {
		    	
		    	log.info("rawPassword: " + rawPassword.toString());
		    	log.info("encoded rawPassword: " + encode(rawPassword));
		    	log.info("encodedPassword: "+ encodedPassword);
		      
		      // remove {CRYPT} prefix
		      //return crypt.matches(rawPassword, encodedPassword.substring(7));
		    	return crypt.matches(rawPassword, encodedPassword);
		    }
		};
	}
	
	
	
//	//Getting values from properties file
//	@Value("${ldap.urls}")
//	private String ldapUrls;
//	
//	@Value("${ldap.base.dn}")
//	private String ldapBaseDn;
//	
//	@Value("${ldap.username}")
//	private String ldapSecurityPrincipal;
//	
//	@Value("${ldap.password}")
//	private String ldapPrincipalPassword;
//	
//	@Value("${ldap.user.dn.pattern}")
//	//private String ldapUserDnPattern ="uid={0},cn=users";
//	private String ldapUserDnPattern;
//	
//	@Value("${ldap.enabled}")
//	private String ldapEnabled;
//
//	// Update configure method for the online test server
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		
//		//System.out.println("value="+ldapUserDnPattern);
//		
//		auth.ldapAuthentication()
//			.userDnPatterns(ldapUserDnPattern)
//			.contextSource()
//				.url(ldapUrls + ldapBaseDn)
//				.managerDn(ldapSecurityPrincipal)
//				.managerPassword(ldapPrincipalPassword)
//			.and()
//				.passwordCompare()
//				.passwordEncoder(new LdapShaPasswordEncoder())
//				//.passwordEncoder(new BCryptPasswordEncoder())
//				.passwordAttribute("userPassword");
//	}

}
