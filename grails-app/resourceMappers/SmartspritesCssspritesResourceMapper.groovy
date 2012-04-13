/**
 *
 * @author Jeff
 */

import org.grails.plugin.resource.mapper.MapperPhase
import org.grails.plugin.resource.ResourceMeta
import org.carrot2.labs.smartsprites.SmartSpritesParameters
import org.carrot2.labs.smartsprites.SpriteBuilder
import org.carrot2.labs.smartsprites.message.MemoryMessageSink
import org.carrot2.labs.smartsprites.message.Message
import org.carrot2.labs.smartsprites.message.MessageLog
import org.grails.plugin.resource.ResourceProcessor
import org.grails.plugin.resource.util.ResourceMetaStore

class SmartspritesCssspritesResourceMapper
{
  def phase = MapperPhase.MUTATION

  ResourceProcessor grailsResourceProcessor
  def grailsApplication


  static defaultExcludes = ['**/*.min.css']
  static defaultIncludes = ['**/*.css']

  def map(resource, config)
  {

    if (config?.disable)
    {
      log.debug "SmartSprites disabled in Config.groovy"
      return false
    }

    if (resource?.processedFile?.name?.startsWith("bundle-"))
    {
      log.debug "Skipping a bundle as the spriting is done on a per css file."
    }
    else
    {

      def realFile = grailsApplication.parentContext.getResource(resource.actualUrl).file.getPath()
      def output = resource?.processedFile?.parentFile?.getPath()
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


      log.debug "\nSmartSprite log:\n$spriteMessages"
      if(sprites.size()>0)
      {
        def cssFile = new File(tempoutput + File.separator + resource?.processedFile?.name)

        //This is the orginial file just updated the processed file and copy it over.
        resource.processedFile = cssFile
        resource.updateActualUrlFromProcessedFile()

        //For each of the files update the resources as needed
        sprites.each
            { String sprite ->
              File file = new File(sprite)

              def filePath = file.path - file.name
              println tempoutput
              println filePath

              def expectedURL = org.beck.util.DirectoryHelper.findDirDiff(tempoutput, filePath)
              println expectedURL
              def expectedurlprefix = resource.originalUrl.minus(resource.processedFile.name)



              def expectedURI = new URI(expectedurlprefix) //Take the expected url prefix that resources plugin will look for
              def newUri = expectedURI.resolve(expectedURL) //Find what path we created out of the expected url resolved against it this deals with the fact we make the sprites and css files in a temp dir


              // make the images created available as resources
              grailsResourceProcessor.resourceInfo.getOrCreateAdHocResource(newUri.path + file.name) {->

                def mod = grailsResourceProcessor.getOrCreateSyntheticOrImplicitModule(true)
                def uri = newUri.path + file.name

                def r = new ResourceMeta(sourceUrl: uri, workDir: grailsResourceProcessor.getWorkDir(), module: mod)

                r.actualUrl = uri
                r.processedFile = file

                r = grailsResourceProcessor.prepareResource(r, true)
                synchronized (mod.resources)
                {
                  // Prevent concurrent requests resulting in multiple additions of same resource
                  // This relates specifically to the ad-hoc resources module
                  if (!mod.resources.find({ x -> x.sourceUrl == r.sourceUrl }))
                  {
                    mod.resources << r
                  }
                }

                return r
              }
            }
      }
    }
  }



}

