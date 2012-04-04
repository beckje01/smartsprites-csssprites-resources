package org.beck.util

/**
 * Created by IntelliJ IDEA.
 * User: beck
 * Date: 3/6/12
 * Time: 1:36 AM
 * To change this template use File | Settings | File Templates.
 */
class DirectoryHelper
{


  public static String findDirDiff(String left, String right)
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
