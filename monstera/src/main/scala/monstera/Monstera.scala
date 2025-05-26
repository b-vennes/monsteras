package monstera

import indigo.shared.materials.Material.Bitmap
import monstera.generated.Assets

final case class Monstera(
    birthday: Day
)

object Monstera:
  def findBitmap(day: Short): Bitmap =
    day match
      case x if x <= 1 => Assets.assets.Monstera1Material
      case 2           => Assets.assets.Monstera2Material
      case 3           => Assets.assets.Monstera3Material
      case 4           => Assets.assets.Monstera4Material
      case 5           => Assets.assets.Monstera5Material
      case 6           => Assets.assets.Monstera6Material
      case 7           => Assets.assets.Monstera7Material
      case 8           => Assets.assets.Monstera8Material
      case 9           => Assets.assets.Monstera9Material
      case _           => Assets.assets.Monstera10Material
