package net.ideahut.springboot.template;

import java.util.regex.Pattern;

public final class AppConstants {
	private AppConstants() {}
	
	// Default
	public static class Default {
		private Default() {}
		public static final String TIMEZONE = "Asia/Jakarta";
	}
	
	// Boolean
	public static class Boolean {
		private Boolean() {}
		public static final Character YES 	= 'Y';
		public static final Character NO 	= 'N';
	}
	
	// Regex
	public static class Regex {
		private Regex() {}
		public static final Pattern EMAIL	= Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	}
	
	// Profile
	public static class Profile {
		private Profile() {}
		public static final String SYSTEM 	= "__SYSTEM__";		
		public static class Status {
			private Status() {}
			public static final char REGISTER	= 'R';
			public static final char ACTIVE		= 'A';
			public static final char INACTIVE 	= 'I';
			public static final char BLOCKED 	= 'B';
		}
		public static class Gender {
			private Gender() {}
			public static final char FEMALE = 'F';
			public static final char MALE 	= 'M';
		}
	}
	
	// Bean
	public static class Bean {
		private Bean() {}
		public static final String DATA_SOURCE 					= "dataSource";
		public static final String ENTITY_MANAGER_FACTORY		= "entityManagerFactory";
		public static final String PERSISTENCE_MANAGED_TYPES	= "persistenceManagedTypes";
		public static final String TRANSACTION_MANAGER			= "transactionManager";
		public static final String DATA_MAPPER					= "dataMapper";
		public static final String ENTITY_TRX_MANAGER			= "entityTrxManager";
		
		public static class Audit {
			private Audit() {}
			public static final String SESSION_FACTORY	= "auditSessionFactory";
			public static final String DATA_SOURCE 		= "auditDataSource";
		}
		
		public static final class Async {
			private Async() {}
			public static final String COMMON	= "commonAsync";
			public static final String AUDIT	= "auditAsync";		
		}
		
		public static final class Admin {
			private Admin() {}
			public static final String HANDLER		= "adminHandler";
			public static final String SECURITY		= "adminSecurity";
			public static final String CREDENTIAL	= "adminCredential";
		}
		
	}
	
}
