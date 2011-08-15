/**
 *
 * @author Jeff
 */

import org.grails.plugin.resource.mapper.MapperPhase
import org.carrot2.labs.smartsprites.SmartSpritesParameters
import org.carrot2.labs.smartsprites.SpriteBuilder
import org.carrot2.labs.smartsprites.message.MemoryMessageSink
import org.carrot2.labs.smartsprites.message.Message
import org.carrot2.labs.smartsprites.message.Message.MessageLevel
import org.carrot2.labs.smartsprites.message.MessageLog

class SmartspritesCssspritesResourceMapper
{
  def phase = MapperPhase.GENERATION
  
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



    def filepath = resource?.processedFile?.parentFile.getPath() + '\\' + resource?.processedFile?.name



    def parameters = new SmartSpritesParameters()
    parameters.cssFileSuffix = SPRITE_CSS_SUFFIX

    
    def cssFiles = [filepath]

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

