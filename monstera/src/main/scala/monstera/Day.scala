package monstera

import java.time.{LocalDate, ZoneOffset}

final case class Day(
    dayOfYear: Short,
    year: Short
)

object Day:
  def today: Day =
    val now = LocalDate.now(ZoneOffset.UTC)
    Day(
      now.getDayOfYear.toShort,
      now.getYear.toShort
    )
