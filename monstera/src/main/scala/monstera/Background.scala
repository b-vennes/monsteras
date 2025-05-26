package monstera

import indigo.shared.materials.Material
import indigo.shared.time.Millis
import monstera.generated.Assets

final case class Background(
    index: Int,
    timeSinceLastUpdate: Millis,
    updateEvery: Millis
):
  def render: Material.Bitmap =
    index match
      case 0 => Assets.assets.Bg1Material
      case 1 => Assets.assets.Bg2Material
      case _ => Assets.assets.Bg3Material

  def update(delta: Millis): Background =
    val newDelta = timeSinceLastUpdate + delta

    if newDelta >= updateEvery
    then
      copy(
        index = (index + 1) % Background.frames,
        timeSinceLastUpdate = newDelta - updateEvery
      )
    else copy(timeSinceLastUpdate = newDelta)

object Background:
  val frames: Int = 3
