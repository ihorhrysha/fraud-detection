package ua.ucu.edu.provider

import ua.ucu.edu.model._

trait BlackDataProviderApi {

  def isBlack(user: User): Boolean
}
