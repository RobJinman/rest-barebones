#com.recursiveloop.webcommon

Common web related code. The project also contains a demo app -- a barebones RESTful Java EE web application intended to serve as the back-end of a Single Page Application or web service.

##Environment setup

###Postgresql Database Setup

Install the database server

        sudo apt-get install postgresql
        sudo apt-get install postgresql-contrib

Install the PgAdminIII client

        sudo apt-get install pgadmin3

From the dbSetup directory, run

        sudo ./create.sh password

This will create the user rlwebapp with password "password" and two databases, maindb and testdb.

Application settings are stored in the rl.config table. These will need to be edited for some features to work, e.g. confirmation emails. Use PgAdminIII to edit the database.

###Java 8

Download tarball from Oracle's website and extract to /opt.

Run the following:

        sudo update-alternatives --install "/usr/bin/java" "java" "/opt/jdk1.8.0_73/jre/bin/java" 1
        sudo update-alternatives --install "/usr/bin/javac" "javac" "/opt/jdk1.8.0_73/bin/javac" 1
        sudo update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/jdk1.8.0_73/jre/bin/javaws" 1
        sudo update-alternatives --set java /opt/jdk1.8.0_73/jre/bin/java
        sudo update-alternatives --set javac /opt/jdk1.8.0_73/bin/javac
        sudo update-alternatives --set javaws /opt/jdk1.8.0_73/jre/bin/javaws

###WildFly 10.x

Download Wildfly 10 and extract to /opt.

Install the Postgresql module. This involves copying the Postgresql JDBC driver to wildfly-10.0.0.Final/modules/org/postgresql/main and adding a module.xml file alongside it with the following contents:

        <?xml version="1.0" ?>
        <module xmlns="urn:jboss:module:1.1" name="org.postgresql">
            <resources>
                <resource-root path="postgresql-9.4.1208.jar"/>
            </resources>
            <dependencies>
                <module name="javax.api"/>
                <module name="javax.transaction.api"/>
            </dependencies>
        </module>

Configure the datasources in containerConfig/wildfly10/standalone.xml. Add both a main database and a test database.

        <subsystem xmlns="urn:jboss:domain:datasources:2.0">
            <datasources>
                <datasource jta="true" jndi-name="java:jboss/jdbc/maindb" pool-name="maindb" enabled="true" use-java-context="true" use-ccm="true">
                    <connection-url>jdbc:postgresql://localhost:5432/maindb</connection-url>
                    <driver>org.postgresql</driver>
                    <pool>
                        <min-pool-size>1</min-pool-size>
                        <max-pool-size>30</max-pool-size>
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
                        <max-pool-size>30</max-pool-size>
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
            </datasources>
        </subsystem>

Because the datasource's JNDI name is container-specific, the application provides mappings to jdbc/maindb and jdbc/testdb by placing resource-ref elements in WEB-INF/web.xml and WEB-INF/jboss-web.xml.

###Jenkins Setup

Install Jenkins CI Server

        wget -q -O - https://jenkins-ci.org/debian/jenkins-ci.org.key | sudo apt-key add -
        sudo sh -c 'echo deb http://pkg.jenkins-ci.org/debian binary/ > /etc/apt/sources.list.d/jenkins.list'
        sudo apt-get update
        sudo apt-get install jenkins

Change the default port (e.g. to 8082) by editing /etc/default/jenkins and restarting

        sudo service jenkins restart

Navigate to localhost:8082, go to Manage Jenkins -> Manage Plugins and install the following

* Git plugin
* Gradle plugin
* Parameterized trigger plugin
* Build name setter plugin
* Clone workspace SCM plugin
* Build pipeline plugin

Install any updates

###Artifactory Setup

Install JFrog artifactory

* Download and extract zip to /opt/
* Run bin/installService.sh


##To Build Locally

To build, type

        gradle build

To run locally, type

        gradle cargorunlocal

##To Build on Jenkins CI Server

The project has dependencies on buildcommon and corsfilter. These will need to reside in the Artifactory repo already.

Create three jobs

* webcommon-initial
* webcommon-integ-tests
* webcommon-distribution

Configure webcommon-initial as follows:

* For source code management, select Git and enter the repo URL. Build all branches and set polling to something like * * * * * (once per minute).
* Add the build step Invoke Gradle Script, and select Use Gradle Wrapper. Make the script executable. Add the switch --stacktrace, and specify the tasks clean and test.
* Add the post-build action Archive for Clone Workspace SCM. Include all files with the pattern **/*.
* Add the post-build action Publish JUnit test result report. The XML files are located at build/test-results/unit/*.xml.
* Add the post-build action Trigger parameterized build on other projects. Set to trigger webcommon-integ-tests when stable. Add a predefined parameter SOURCE_BUILD_NUMBER=${BUILD_NUMBER}.

Configure webcommon-integ-tests as follows:

* Clone the workspace from webcommon-initial
* Execute the task integrationTest
* Publish JUnit test result report. The file pattern should be build/test-results/integration/*.xml.
* Trigger webcommon-distribution and pass through the current build parameters.

Configure webcommon-distribution as follows:

* Clone the workspace from webcommon-initial
* Execute the task publishWebcommonPublicationToArtifactoryRepository

Create the pipeline webcommon and select webcommon-initial as the initial job.

