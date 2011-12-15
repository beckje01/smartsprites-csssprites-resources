/**
 *
 * @author Jeff
 */

import org.grails.plugin.resource.mapper.MapperPhase
import org.grails.plugin.resource.ResourceService
import org.grails.plugin.resource.ResourceMeta
import org.carrot2.labs.smartsprites.SmartSpritesParameters
import org.carrot2.labs.smartsprites.SpriteBuilder
import org.carrot2.labs.smartsprites.message.MemoryMessageSink
import org.carrot2.labs.smartsprites.message.Message
import org.carrot2.labs.smartsprites.message.Message.MessageLevel
import org.carrot2.labs.smartsprites.message.MessageLog
import org.apache.ivy.plugins.matcher.ExactPatternMatcher

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

    if (config?.disable)
    {
      if (log.debugEnabled)
      {
        log.debug "SmartSprites  disabled in Config.groovy"
      }
      return false
    }

    if (resource?.processedFile?.name.startsWith("bundle-"))
    {
      //TODO change to logging
      println "Skipping a bundle as the spriting is done on a per css file."
    }
    else
    {

      def realFile = grailsApplication.parentContext.getResource(resource.actualUrl).file.getPath()
      def output = resource?.processedFile.parentFile?.getPath()
      def tempoutput = output + File.separator + "spritetemp"

      def parameters = new SmartSpritesParameters()

      parameters.cssFileSuffix = ""
      parameters.rootDir = grailsApplication.parentContext.getResource(resource.actualUrl).file.parentFile.getPath()
      parameters.outputDir = tempoutput

      def cssFiles = [realFile]

      parameters.cssFiles = cssFiles


      def messageSink = new MemoryMessageSink()
      def messageLog = new MessageLog(messageSink)

      def sprites = []


      new SpriteBuilder(parameters, messageLog).buildSprites()

      def comparator = Message.MessageLevel.COMPARATOR
      def minLevel = Message.MessageLevel.INFO
      StringBuilder spriteMessages = new StringBuilder()
      for (message in messageSink.messages)
      {
        if (comparator.compare(message.level, minLevel) >= 0)
        {
          if (message.type == Message.MessageType.WRITING_SPRITE_IMAGE)
          {
            def msg = message.getFormattedMessage()
            def sprite = msg.replaceFirst("Writing sprite image of size [0-9]* x [0-9]* for sprite '.*' to", '') //Got this message format from https://github.com/carrotsearch/smartsprites/blob/master/src/main/java/org/carrot2/labs/smartsprites/message/Message.java
            sprites << sprite.trim()
          }
          spriteMessages.append '\t'
          spriteMessages.append message.toString()
          spriteMessages.append '\n'
        }
      }

      //TODO change to logging
      println "\nSmartSprite log:\n$spriteMessages"

      //println tempoutput + File.separator + resource?.processedFile?.name
      def cssFile = new File(tempoutput + File.separator + resource?.processedFile?.name)

      //This is the orginial file just updated the processed file and copy it over.
      resource.processedFile = cssFile
      resource.updateActualUrlFromProcessedFile()

      //For each of the files update the resources as needed
      sprites.each
          { String sprite ->
            File file = new File(sprite)

            def filePath = file.path - file.name
            def expectedURL = findDirDiff(tempoutput, filePath)

            def expectedurlprefix = resource.originalUrl.minus(resource.processedFile.name)



            def expectedURI = new URI(expectedurlprefix) //Take the expected url prefix that resources plugin will look for
            def newUri = expectedURI.resolve(expectedURL) //Find what path we created out of the expected url resolved against it this deals with the fact we make the sprites and css files in a temp dir

            println newUri.path + file.name

            // make the images created available as resources
            def fileResource = resourceService.findSyntheticResourceForURI(newUri.path + file.name)
            if (!fileResource)
            {
              fileResource = resourceService.newSyntheticResource(newUri.path + file.name, ResourceMeta)
              fileResource.processedFile = file
            }


          }

    }
  }


  private String findDirDiff(String left, String right)
  {
    //TODO consider using something like the relativize in URI

    def sep = File.separator
    if (sep == "\\")     //I hate all these \ but can't use slashy strings due to not being able to have \ as the last char
    {
      sep = "\\\\"
    }

    def leftParts = left.split(sep)
    def rightParts = right.split(sep)


    int i = 0

    while (true) //TODO this could be redone to be tighter only really need the position of the difference and then just run the loops you see in the difference section
    {

      if (i >= leftParts.length) //check if out of destinationParts
      {//We are out of destination parts and they have all matched up to this point so we can just use the rest of the sprite

        def out = ""

        for (def x = i; x < rightParts.length; x++)
        {
          out += rightParts[x] + '/'
        }
        return out
      }
      else if (i >= rightParts.length)
      {//We are out of spriteparts so we need to start adding the ..
        //return "adding .."
        def out = ""
        for (def x = i; x < leftParts.length; x++)
        {
          out += "../"
        }
        return out
      }
      else
      { //We have some of each so check for matching


        if (leftParts[i] == rightParts[i])
        {//Its a match move on
          i++
        }
        else
        {//No more match figure out the needed .. based on whats left of the sprite parts and add whats left to the
          def out = ""
          for (def x = i; x < leftParts.length; x++)
          {
            out += "../"
          }
          for (def x = i; x < rightParts.length; x++)
          {
            out += rightParts[x] + '/'
          }
          return out
        }
      }
    }


  }
}

