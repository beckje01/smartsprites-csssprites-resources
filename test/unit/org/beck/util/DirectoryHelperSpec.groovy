package org.beck.util

import grails.plugin.spock.UnitSpec
import spock.lang.Unroll

/**
 * Created by IntelliJ IDEA.
 * User: beck
 * Date: 3/6/12
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
class DirectoryHelperSpec extends UnitSpec
{

  @Unroll("#left - #right ")
  def "findDirDiff"()
  {
    given:
        def res = org.beck.util.DirectoryHelper.findDirDiff(left,right)

    expect: res == expected

    where:
    left     |right   |expected
    ".."+File.separator | File.separator  | "../"
    File.separator+"grails-resources"+File.separator+"css"+File.separator+"spritetemp" | File.separator+"grails-resources"+File.separator+"css"+File.separator+"img"+File.separator  | "../img/"
  }

}
