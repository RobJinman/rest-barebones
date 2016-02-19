Database Setup
==============


Install Postgresql and pgadmin3
-------------------------------

        sudo apt-get install postgresql
        sudo apt-get install postgresql-contrib
        sudo apt-get install pgadmin3


Enable Password Authentication
------------------------------

        Open /etc/postgresql/9.3/main/pg_hba.conf and change 'peer' to 'md5' for all local users


Create Databases
----------------

        sudo create.sh password

This will create the databases maindb and testdb, and the user rlwebapp.


Configure Datasource for Wildfly 8
----------------------------------

Copy the postgres JDBC driver to modules/org/postgresql/main and supply an accompanying module.xml file containing

        <?xml version="1.0" ?>
        <module xmlns="urn:jboss:module:1.1" name="org.postgresql">
            <resources>
                <resource-root path="postgresql-9.3-1101.jdbc41.jar"/>
            </resources>
            <dependencies>
                <module name="javax.api"/>
                <module name="javax.transaction.api"/>
            </dependencies>
        </module>

Insert the following into standalone.xml under 'datasources'.

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
                <password>password</password>
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
                <password>password</password>
            </security>
        </datasource>
        <drivers>
            <driver name="org.postgresql" module="org.postgresql">
                <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
            </driver>
        </drivers>

