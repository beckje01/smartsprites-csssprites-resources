grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsPlugins()
    grailsRepo "http://grails.org/plugins"
    grailsHome()
    grailsCentral()
    mavenCentral()

  }
  dependencies {
    runtime 'com.carrotsearch:smartsprites:0.2.8'

  }

  plugins {
    compile(":spock:0.6") {
      export = false
    }
    runtime(":resources:1.1.6")
    {
      export = false
    }
    compile(":codenarc:0.17")
    {
      export = false
    }
    build(":release:2.0.3")
    {
      export = false
    }
  }
}
