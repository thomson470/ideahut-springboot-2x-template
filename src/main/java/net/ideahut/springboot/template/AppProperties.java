package net.ideahut.springboot.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.audit.DatabaseAuditProperties;
import net.ideahut.springboot.cache.CacheGroupProperties;
import net.ideahut.springboot.mail.MailProperties;
import net.ideahut.springboot.redis.RedisProperties;
import net.ideahut.springboot.security.SecurityUser;
import net.ideahut.springboot.task.TaskProperties;

@Configuration
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
public class AppProperties {
	
	private String instanceId;
	private Boolean loggingError;
	private String gridLocation;
	private Database otherDb = new Database();
	private Audit audit = new Audit();
	private Map<String, String> cors = new HashMap<>();
	private Async async = new Async();
	private List<Class<?>> ignoredHandlerClasses = new ArrayList<>();
	
	private RedisProperties redis = new RedisProperties();
	private MailProperties mail = new MailProperties();
	private Cache cache = new Cache();
	private Admin admin = new Admin();
	
	
	@Setter
	@Getter
	public static class Cache {
		private List<CacheGroupProperties> groups = new ArrayList<>();
	}
	
	@Setter
	@Getter
	public static class Audit extends Database {
		private DatabaseAuditProperties properties = new DatabaseAuditProperties();
	}
	
	@Setter
	@Getter
	public static class Database {
		private Datasource datasource = new Datasource();
		private Jpa jpa = new Jpa();
	}
	
	@Setter
	@Getter
	public static class Datasource {
		private String jndiName;
		private String driverClassName;
		private String jdbcUrl;
		private String username;
		private String password;
	}
	
	@Setter
	@Getter
	public static class Jpa {
		private String database;
		private Map<String, String> properties = new HashMap<>();
	}
	
	@Setter
	@Getter
	public static class Async {
		private TaskProperties common 	= new TaskProperties();
		private TaskProperties audit 	= new TaskProperties();
	}
	
	@Setter
	@Getter
	public static class Admin extends AdminProperties {
		private String configFile;
		private String passwordType;
		private Integer expiryInMinutes;
		private List<SecurityUser> users = new ArrayList<>();
	}
	
}
