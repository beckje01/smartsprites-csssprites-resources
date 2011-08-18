/**
 *
 * @author Jeff
 */

import org.grails.plugin.resource.mapper.MapperPhase
import org.grails.plugin.resource.ResourceService
import org.carrot2.labs.smartsprites.SmartSpritesParameters
import org.carrot2.labs.smartsprites.SpriteBuilder
import org.carrot2.labs.smartsprites.message.MemoryMessageSink
import org.carrot2.labs.smartsprites.message.Message
import org.carrot2.labs.smartsprites.message.Message.MessageLevel
import org.carrot2.labs.smartsprites.message.MessageLog

class SmartspritesCssspritesResourceMapper
{
  def phase = MapperPhase.MUTATION
  def resourceService
  def grailsApplication
  private static final String SPRITE_CSS_SUFFIX = '___sprite_temp___'

  static defaultExcludes = ['**/*.min.css']
  static defaultIncludes = ['**/*.css']
  
  def map(resource, config)  
  {

    if(config?.disable)
    {
      if (log.debugEnabled) 
      {
        log.debug "SmartSprites  disabled in Config.groovy"
      }
      return false
    }

    if(resource?.processedFile?.name.startsWith("bundle-"))
    {
      println "Skipping a bundle as the spriting is done on a per css file."
    }
    else
    {

      def realFile = grailsApplication.parentContext.getResource(resource.actualUrl).file.getPath()

      def parameters = new SmartSpritesParameters()
      parameters.cssFileSuffix = ""
      parameters.rootDir = grailsApplication.parentContext.getResource(resource.actualUrl).file.parentFile.getPath()
      parameters.outputDir = resource?.processedFile.parentFile?.getPath()
    
      def cssFiles = [realFile]

      parameters.cssFiles = cssFiles


      def messageSink = new MemoryMessageSink()
      def messageLog = new MessageLog(messageSink)


      new SpriteBuilder(parameters, messageLog).buildSprites()

      def comparator = Message.MessageLevel.COMPARATOR
      def minLevel = Message.MessageLevel.IE6NOTICE
      StringBuilder spriteMessages = new StringBuilder()
      for (message in messageSink.messages) {
        if (comparator.compare(message.level, minLevel) >= 0) {
          spriteMessages.append '\t'
          spriteMessages.append message.toString()
          spriteMessages.append '\n'
        }
      }


      println "\nSmartSprite log:\n$spriteMessages"



    }
  }

}

