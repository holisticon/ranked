package de.holisticon.ranked

import javax.ws.rs.core.{MediaType, UriInfo}
import scala.collection.JavaConverters._
import java.util.Collections
import javax.ws.rs.ext.Providers


/**
 * Helper methods for ResourceFacade GET-contract
 * @author Daniel
 */
object ResourceFacadeHelper {

  def extractExpand(uriInfo:UriInfo):Seq[String] = Option(uriInfo.getQueryParameters.get("expand")).getOrElse(Collections.emptyList()).asScala.flatMap(_.split(""","""))
  def extractStartIndex(uriInfo:UriInfo):Int = Option(uriInfo.getQueryParameters.getFirst("start-index")).map(Integer.parseInt).getOrElse(0)
  def extractMaxResults(uriInfo:UriInfo):Int = Option(uriInfo.getQueryParameters.getFirst("max-results")).map(Integer.parseInt).getOrElse(Integer.MAX_VALUE)



}
