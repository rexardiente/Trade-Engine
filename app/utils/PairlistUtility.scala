package utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._
import com.typesafe.config.ConfigFactory
import mrtradelibrary.utils.Currencies
import mrtradelibrary.models.domain.Pairlist

class PairlistUtility {
  def getCurrencyPair: Future[Seq[Pairlist]] = {
      for {
        currencyPair <- Future.successful {
          ConfigFactory.load().getStringList("currencyPair").toString
        }

        handlePair <- Future.successful {
          currencyPair.replaceAll("[\\[|,|,]|]", "").trim().split("\\s+")
        }

        pairs <- Future.successful {
          handlePair.map { count =>
            Pairlist(
              currencyEnum(count.substring(0,3)).getOrElse(Currencies.Xrp),
              currencyEnum(count.substring(4,7)).getOrElse(Currencies.Jpy))
          }.toSeq
        }
      } yield pairs
    }

  def currencyEnum(txt: String): Option[Currencies.Value] = Currencies.values.find(_.toString == txt)
}
