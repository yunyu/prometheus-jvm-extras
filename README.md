# Prometheus JVM Extras


Adds disk space metrics for the default file system, as well as the missing ones from [OperatingSystemMXBean](https://docs.oracle.com/javase/7/docs/jre/api/management/extension/com/sun/management/OperatingSystemMXBean.html).

## Usage

Add the following to your pom.xml:

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

	<dependency>
	    <groupId>com.github.yunyu</groupId>
	    <artifactId>prometheus-jvm-extras</artifactId>
	    <version>1.0.0-SNAPSHOT</version>
	</dependency>

Then register the collector in your application:

    new AdditionalJVMExports().register();
