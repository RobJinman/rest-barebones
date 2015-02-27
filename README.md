#rest-barebones

A barebones RESTful Java EE web application intended to serve as the back-end of a Single Page Application or web service. Originally part of the JLoop project.

To build, type

    gradle build

To run locally, type

    gradle cargorunlocal

##Environment setup

###WildFly 8.x

Install the Postgresql module. This involves copying the Postgresql JDBC driver to wildfly-8.2.0.Final/modules/org/postgresql/main and adding a module.xml file alongside it with the following contents:

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

Configure the DataSource in standalone.xml:

    <subsystem xmlns="urn:jboss:domain:datasources:2.0">
        <datasources>
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
                    <user-name>deploy</user-name>
                    <password>78fec^a19be79d7e!31be32f</password>
                </security>
            </datasource>
            ...
            <drivers>
                <driver name="org.postgresql" module="org.postgresql">
                    <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
                </driver>
                ...
            </drivers>
        </datasources>
    </subsystem>

Because the DataSource's JNDI name is container-specific, the application provides a mapping to jdbc/testdb by placing a resource-ref in WEB-INF/web.xml and WEB-INF/jboss-web.xml.
