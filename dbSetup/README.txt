INSTALL POSTGRESQL AND PGADMIN3
  sudo apt-get install postgresql
  sudo apt-get install postgresql-contrib
  sudo apt-get install pgadmin3

ENABLE PASSWORD AUTHENTICATION
  Open /etc/postgresql/9.3/main/pg_hba.conf and change 'peer' to 'md5' for all local users

CREATE DATABASES AND RLWEBAPP USER
  sudo create.sh testdb
  sudo create.sh maindb <password>

CONFIGURE DATASOURCE FOR CONTAINER
  For Wildfly8x this involves inserting the following into standalone.xml under 'datasources'.

  <datasource jta="true" jndi-name="java:jboss/jdbc/maindb" pool-name="maindb" enabled="true" use-java-context="true" use-ccm="true">
      <connection-url>jdbc:postgresql://localhost:5432/maindb</connection-url>
      <driver>org.postgresql</driver>
      <pool>
          <min-pool-size>1</min-pool-size>
          <max-pool-size>4</max-pool-size>
          <prefill>false</prefill>
          <use-strict-min>false</use-strict-min>
          <flush-strategy>FailingConnectionOnly</flush-strategy>
      </pool>
      <security>
          <user-name>rlwebapp</user-name>
          <password>password123</password>
      </security>
  </datasource>
  <datasource jta="true" jndi-name="java:jboss/jdbc/testdb" pool-name="testdb" enabled="true" use-java-context="true" use-ccm="true">
      <connection-url>jdbc:postgresql://localhost:5432/testdb</connection-url>
      <driver>org.postgresql</driver>
      <pool>
          <min-pool-size>1</min-pool-size>
          <max-pool-size>4</max-pool-size>
          <prefill>false</prefill>
          <use-strict-min>false</use-strict-min>
          <flush-strategy>FailingConnectionOnly</flush-strategy>
      </pool>
      <security>
          <user-name>rlwebapp</user-name>
          <password>password123</password>
      </security>
  </datasource>
  <drivers>
      <driver name="org.postgresql" module="org.postgresql">
          <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
      </driver>
  </drivers>
