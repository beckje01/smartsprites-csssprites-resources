class SmartSpritesResourcesGrailsPlugin
{
  // the plugin version
  def version = "0.3"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.3.7 > *" //TODO evaluate the min version of Grails that this supports.
  // the other plugins this plugin depends on
  def dependsOn = [resources: '1.0 > *']
  def loadAfter = ['resources']
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
      "grails-app/views/error.gsp",
      "grails-app/views/**",
      "grails-app/domain/**",
      "grails-app/services/test/**",
      "test/**"
  ]


  def author = "Jeff Beck and Colin Harrington"
  def authorEmail = "grails.smart.sprite.resources@gmail.com"
  def title = "Smart Sprites Resources"
  def description = '''Creates Sprites using SmartSprites as a component of the Resources plugin.'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/smart-sprites-resources" //TODO put some Documentation into Github docs like spring-security-core or others.

  def issueManagement = [system: 'github', url: "https://github.com/beckje01/smartsprites-csssprites-resources/issues"]
  def scm = [url: "https://github.com/beckje01/smartsprites-csssprites-resources"]


}
