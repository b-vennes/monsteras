package monstera

final case class Model(
    currentDay: Day,
    monstera: Monstera
):
  val monsteraAge: Short =
    (
      ((currentDay.year - monstera.birthday.year) * 365) +
        (currentDay.dayOfYear - monstera.birthday.dayOfYear)
    ).toShort
